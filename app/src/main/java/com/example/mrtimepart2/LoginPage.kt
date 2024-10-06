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

class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Pre-existing admin account (only create if no accounts exist)
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        if (sharedPref.all.isEmpty()) {
            val editor = sharedPref.edit()
            editor.putString("email", "admin")
            editor.putString("password", "admin")
            editor.apply()
        }

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
}

@Composable
fun LoginPageLayout(context: Context) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val mainLayout = LinearLayout(ctx).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.parseColor("#222121"))
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(40, 0, 40, 0)
            }

            // Logo
            val logo = ImageView(ctx).apply {
                setImageResource(R.drawable.mr_time_removebg)
            }
            mainLayout.addView(logo, LinearLayout.LayoutParams(
                500,  // Set width to 100dp
                500  // Set height to 100dp
            ).apply {
                topMargin = 50
                gravity = Gravity.CENTER_HORIZONTAL
                weight = 2f
            })

            // Sign In Text
            val signInText = TextView(ctx).apply {
                text = "Sign In"
                setTextColor(Color.WHITE)
                textSize = 24f
            }
            mainLayout.addView(signInText, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 20
                weight = 1f
            })

            // Welcome Text
            val welcomeText = TextView(ctx).apply {
                text = "Hi there! Nice to see you again"
                setTextColor(Color.LTGRAY)
                textSize = 16f
            }
            mainLayout.addView(welcomeText, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            })

            // Email Input Field
            val editTextEmail = EditText(ctx).apply {
                hint = "Email"
                setHintTextColor(Color.GRAY)
                setTextColor(Color.WHITE)
            }
            mainLayout.addView(editTextEmail, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 20
                weight = 1f
            })

            // Password Input Field
            val editTextPassword = EditText(ctx).apply {
                hint = "Password"
                setHintTextColor(Color.GRAY)
                setTextColor(Color.WHITE)
                inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            mainLayout.addView(editTextPassword, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 20
                weight = 1f
            })

            // Sign In Button
            val buttonSignIn = Button(ctx).apply {
                text = "Sign In"
                setBackgroundColor(Color.parseColor("#F44336"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    val email = editTextEmail.text.toString().trim().toLowerCase()
                    val password = editTextPassword.text.toString().trim()

                    val sharedPref = ctx.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    val savedEmail = sharedPref.getString("email", "").orEmpty().toLowerCase()
                    val savedPassword = sharedPref.getString("password", "").orEmpty()

                    Log.d("LoginPage", "Entered email: $email, saved email: $savedEmail")
                    Log.d("LoginPage", "Entered password: $password, saved password: $savedPassword")

                    if (email == savedEmail && password == savedPassword) {
                        ctx.startActivity(Intent(ctx, MainActivity::class.java))
                        (ctx as LoginPage).finish()
                    } else {
                        Toast.makeText(ctx, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            mainLayout.addView(buttonSignIn, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 20
                weight = 1f
            })

            // Create Account Button
            val buttonCreateAccount = Button(ctx).apply {
                text = "Create Account"
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    ctx.startActivity(Intent(ctx, CreateAccountActivity::class.java))
                }
            }
            mainLayout.addView(buttonCreateAccount, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 10
                weight = 1f
            })

            mainLayout
        },
        update = { view ->
            // Update the view if needed
        }
    )
}