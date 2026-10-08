package com.example.otlobmentamer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser == null) {
            goToLogin()
            return
        }

        val nameEditText =
            findViewById<EditText>(R.id.profileNameEditText)

        val emailEditText =
            findViewById<EditText>(R.id.profileEmailEditText)

        val addressEditText =
            findViewById<EditText>(R.id.profileAddressEditText)

        val phoneEditText =
            findViewById<EditText>(R.id.profilePhoneEditText)

        val saveProfileButton =
            findViewById<Button>(R.id.saveProfileButton)

        val backToLoginButton =
            findViewById<Button>(R.id.backToLoginButton)

        emailEditText.setText(currentUser.email)

        val userId = currentUser.uid

        val userReference = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)

        userReference.get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {

                    nameEditText.setText(
                        snapshot.child("name").value?.toString() ?: ""
                    )

                    addressEditText.setText(
                        snapshot.child("address").value?.toString() ?: ""
                    )

                    phoneEditText.setText(
                        snapshot.child("phone").value?.toString() ?: ""
                    )
                }
            }

        saveProfileButton.setOnClickListener {

            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (name.isEmpty() ||
                address.isEmpty() ||
                phone.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val user = HashMap<String, String>()

            user["name"] = name
            user["email"] = email
            user["address"] = address
            user["phone"] = phone

            userReference.setValue(user)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Profile saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this,
                        SuccessActivity::class.java
                    )

                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { error ->

                    Toast.makeText(
                        this,
                        "Database error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        backToLoginButton.setOnClickListener {
            goToLogin()
        }
    }

    private fun goToLogin() {

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}