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
import android.widget.ImageView
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

class LoginPage : ComponentActivity() {

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
                    LoginPageLayout(this)
                }
            }
        }
    }

    @Composable
    fun LoginPageLayout(context: Context) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val mainLayout = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    setBackgroundColor(Color.parseColor("#0C1424"))
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(40, 0, 40, 0)
                }

                // Logo
                val logo = ImageView(ctx).apply {
                    setImageResource(R.drawable.mr_time)
                }
                mainLayout.addView(logo, LinearLayout.LayoutParams(600, 600).apply {
                    topMargin = 50
                    weight = 2f
                })

                val signInText = TextView(ctx).apply {
                    text = "Sign In"
                    setTextColor(Color.parseColor("#e45a66"))
                    textSize = 24f
                }
                mainLayout.addView(signInText)

                val welcomeText = TextView(ctx).apply {
                    text = "Hi there! Nice to see you again"
                    setTextColor(Color.parseColor("#e45a66"))
                    textSize = 16f
                }
                mainLayout.addView(welcomeText)

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

                val buttonSignIn = Button(ctx).apply {
                    text = "Sign In"
                    setBackgroundColor(Color.parseColor("#e45a66"))
                    setTextColor(Color.parseColor("#FFFFFFFF"))
                    setOnClickListener {
                        val email = editTextEmail.text.toString().trim()
                        val password = editTextPassword.text.toString().trim()

                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    ctx.startActivity(Intent(ctx, MainActivity::class.java))
                                    (ctx as LoginPage).finish()
                                } else {
                                    Toast.makeText(ctx, "Invalid email or password", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                mainLayout.addView(buttonSignIn)

                val buttonCreateAccount = Button(ctx).apply {
                    text = "Create Account"
                    setBackgroundColor(Color.parseColor("#e45a66"))
                    setTextColor(Color.parseColor("#FFFFFFFF"))
                    setOnClickListener {
                        ctx.startActivity(Intent(ctx, CreateAccountActivity::class.java))
                    }
                }
                mainLayout.addView(buttonCreateAccount)

                mainLayout
            },
            update = {}
        )
    }
}
