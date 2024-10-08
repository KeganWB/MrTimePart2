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

class CreateAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                mainLayout.addView(createAccountText, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 20
                    weight = 1f
                })

                val editTextEmail = EditText(ctx).apply {
                    hint = "Email"
                    setHintTextColor(Color.GRAY)
                    setTextColor(Color.parseColor("#e45a66"))
                }
                mainLayout.addView(editTextEmail, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 20
                    weight = 1f
                })

                val editTextPassword = EditText(ctx).apply {
                    hint = "Password"
                    setHintTextColor(Color.GRAY)
                    setTextColor(Color.parseColor("#e45a66"))
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

                val editTextConfirmPassword = EditText(ctx).apply {
                    hint = "Confirm Password"
                    setHintTextColor(Color.GRAY)
                    setTextColor(Color.parseColor("#e45a66"))
                    inputType = InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                mainLayout.addView(editTextConfirmPassword, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 20
                    weight = 1f
                })

                val buttonCreateAccount = Button(ctx).apply {
                    text = "Create Account"
                    setBackgroundColor(Color.parseColor("#e45a66")) // Green color
                    setTextColor(Color.parseColor("#FFFFFFFF"))
                    setOnClickListener {
                        val email = editTextEmail.text.toString().trim().toLowerCase()
                        val password = editTextPassword.text.toString().trim()
                        val confirmPassword = editTextConfirmPassword.text.toString().trim()

                        if (password == confirmPassword) {
                            val sharedPref =
                                ctx.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                            val editor = sharedPref.edit()
                            editor.putString("email", email)
                            editor.putString("password", password)
                            editor.apply()

                            Log.d("CreateAccountActivity", "Email: $email, Password: $password")

                            Toast.makeText(ctx, "Account created successfully", Toast.LENGTH_SHORT)
                                .show()
                            ctx.startActivity(Intent(ctx, LoginPage::class.java))
                            (ctx as CreateAccountActivity).finish()
                        } else {
                            Toast.makeText(ctx, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                mainLayout.addView(buttonCreateAccount, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 20
                    weight = 1f
                })

                mainLayout
            },
            update = { view ->
            }
        )
    }
}