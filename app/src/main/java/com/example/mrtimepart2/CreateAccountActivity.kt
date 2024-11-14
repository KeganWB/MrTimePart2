package com.example.mrtimepart2

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CreateAccountLayout(this)
                }
            }
        }
    }

    @Composable
    fun CreateAccountLayout(context: Context) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val mainLayout = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    setBackgroundColor(Color.parseColor("#0C1424"))
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(40, 0, 40, 0)
                }

                val createAccountText = TextView(ctx).apply {
                    text = "Create Account"
                    setTextColor(Color.parseColor("#e45a66"))
                    textSize = 24f
                }
                mainLayout.addView(createAccountText)

                val editTextEmail = EditText(ctx).apply {
                    hint = "Email"
                    setHintTextColor(Color.GRAY)
                    setTextColor(Color.parseColor("#e45a66"))
                }
                mainLayout.addView(editTextEmail)

                val editTextPassword = EditText(ctx).apply {
                    hint = "Password"
                    setHintTextColor(Color.GRAY)
                    setTextColor(Color.parseColor("#e45a66"))
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                mainLayout.addView(editTextPassword)

                val editTextConfirmPassword = EditText(ctx).apply {
                    hint = "Confirm Password"
                    setHintTextColor(Color.GRAY)
                    setTextColor(Color.parseColor("#e45a66"))
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                mainLayout.addView(editTextConfirmPassword)

                val buttonCreateAccount = Button(ctx).apply {
                    text = "Create Account"
                    setBackgroundColor(Color.parseColor("#e45a66"))
                    setTextColor(Color.parseColor("#FFFFFFFF"))
                    setOnClickListener {
                        val email = editTextEmail.text.toString().trim()
                        val password = editTextPassword.text.toString().trim()
                        val confirmPassword = editTextConfirmPassword.text.toString().trim()

                        if (password == confirmPassword) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(ctx, "Account created successfully", Toast.LENGTH_SHORT).show()
                                        ctx.startActivity(Intent(ctx, LoginPage::class.java))
                                        (ctx as CreateAccountActivity).finish()
                                    } else {
                                        Toast.makeText(ctx, "Account creation failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(ctx, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                mainLayout.addView(buttonCreateAccount)

                mainLayout
            },
            update = {}
        )
    }
}
