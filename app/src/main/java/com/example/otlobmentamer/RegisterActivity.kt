package com.example.otlobmentamer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val nameEditText =
            findViewById<EditText>(R.id.nameEditText)

        val emailEditText =
            findViewById<EditText>(R.id.emailEditText)

        val passwordEditText =
            findViewById<EditText>(R.id.passwordEditText)

        val addressEditText =
            findViewById<EditText>(R.id.addressEditText)

        val phoneEditText =
            findViewById<EditText>(R.id.phoneEditText)

        val createAccountButton =
            findViewById<Button>(R.id.createAccountButton)

        val backToLoginButton =
            findViewById<Button>(R.id.backToLoginButton)

        createAccountButton.setOnClickListener {

            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val address = addressEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (name.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
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

            // STEP 1: Create the user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        // Get the UID created by Firebase Authentication
                        val userId = auth.currentUser?.uid

                        if (userId == null) {

                            Toast.makeText(
                                this,
                                "Could not get user ID",
                                Toast.LENGTH_LONG
                            ).show()

                            return@addOnCompleteListener
                        }

                        // STEP 2: Create the user's data for Realtime Database
                        val user = HashMap<String, String>()

                        user["name"] = name
                        user["email"] = email
                        user["address"] = address
                        user["phone"] = phone

                        // STEP 3: Save the data under users/USER_UID
                        FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(userId)
                            .setValue(user)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    this,
                                    "Account created and profile saved!",
                                    Toast.LENGTH_LONG
                                ).show()

                                // STEP 4: Log the user out
                                // so they have to login normally
                                auth.signOut()

                                // STEP 5: Go back to Login
                                val intent = Intent(
                                    this,
                                    MainActivity::class.java
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

                    } else {

                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        backToLoginButton.setOnClickListener {

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            startActivity(intent)
            finish()
        }
    }
}