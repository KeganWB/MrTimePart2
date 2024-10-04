package com.example.mrtimepart2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mrtimepart2.ui.theme.MrTimePart2Theme

class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MrTimePart2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginPageLayout()
                }
            }
        }
    }
}

@Composable
fun LoginPageLayout() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            // Create the main layout (RelativeLayout)
            val mainLayout = RelativeLayout(context)
            mainLayout.setBackgroundColor(Color.parseColor("#222121"))

            // Add Mr Time logo (replace with your actual logo)
            val logo = ImageView(context)
            logo.setImageResource(R.drawable.mr_time) // Replace with your logo
            val logoParams = RelativeLayout.LayoutParams(
                200, 200 // Adjust size as needed
            )
            logoParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            logoParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            logoParams.topMargin = 50 // Adjust margin as needed
            mainLayout.addView(logo, logoParams)

            // Add "Sign In" text
            val signInText = TextView(context)
            signInText.text = "Sign In"
            signInText.setTextColor(Color.WHITE)
            signInText.textSize = 24f // Adjust size as needed
            val signInTextParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            signInTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            signInTextParams.addRule(RelativeLayout.BELOW, logo.id)
            signInTextParams.topMargin = 20 // Adjust margin as needed
            mainLayout.addView(signInText, signInTextParams)

            // Add "Hi there! Nice to see you again" text
            val welcomeText = TextView(context)
            welcomeText.text = "Hi there! Nice to see you again"
            welcomeText.setTextColor(Color.LTGRAY)
            welcomeText.textSize = 16f // Adjust size as needed
            val welcomeTextParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            welcomeTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            welcomeTextParams.addRule(RelativeLayout.BELOW, signInText.id)
            mainLayout.addView(welcomeText, welcomeTextParams)

            // Add email input field
            val editTextEmail = EditText(context)
            editTextEmail.hint = "Email"
            editTextEmail.setHintTextColor(Color.GRAY)
            editTextEmail.setTextColor(Color.WHITE)
            val emailParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            emailParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            emailParams.addRule(RelativeLayout.BELOW, welcomeText.id)
            emailParams.topMargin = 20 // Adjust margin as needed
            emailParams.leftMargin = 40 // Adjust margin as needed
            emailParams.rightMargin = 40 // Adjust margin as needed
            mainLayout.addView(editTextEmail, emailParams)

            // Add password input field
            val editTextPassword = EditText(context)
            editTextPassword.hint = "Password"
            editTextPassword.setHintTextColor(Color.GRAY)
            editTextPassword.setTextColor(Color.WHITE)
            editTextPassword.inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
            val passwordParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            passwordParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            passwordParams.addRule(RelativeLayout.BELOW, editTextEmail.id)
            passwordParams.topMargin = 20 // Adjust margin as needed
            passwordParams.leftMargin = 40 // Adjust margin as needed
            passwordParams.rightMargin = 40 // Adjust margin as needed
            mainLayout.addView(editTextPassword, passwordParams)

            // Add "Sign In" button
            val buttonSignIn = Button(context)
            buttonSignIn.text = "Sign In"
            buttonSignIn.setBackgroundColor(Color.parseColor("#F44336"))
            buttonSignIn.setTextColor(Color.WHITE)
            val signInButtonParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            signInButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            signInButtonParams.addRule(RelativeLayout.BELOW, editTextPassword.id)
            signInButtonParams.topMargin = 20 // Adjust margin as needed
            signInButtonParams.leftMargin = 40 // Adjust margin as needed
            signInButtonParams.rightMargin = 40 // Adjust margin as needed
            mainLayout.addView(buttonSignIn, signInButtonParams)

            buttonSignIn.setOnClickListener {
                // *** Directly start MainActivity ***
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as LoginPage).finish() // Finish LoginActivity
            }

            // Add social login buttons (replace with your actual implementation)
            // For example, for Twitter:
            val twitterButton = Button(context)
            twitterButton.text = "Twitter"
            // ... set other properties like background color, icon, etc. ...
            val twitterButtonParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            // ... add rules for positioning ...
            mainLayout.addView(twitterButton, twitterButtonParams)

            // Similarly, add Facebook button and other UI elements

            // Add "Forgot Password?" and "Sign Up" links
            val forgotPasswordText = TextView(context)
            forgotPasswordText.text = "Forgot Password?"
            // ... set other properties like color, style, etc. ...
            val forgotPasswordParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            // ... add rules for positioning ...
            mainLayout.addView(forgotPasswordText, forgotPasswordParams)

            // Similarly, add "Sign Up" link

            mainLayout // Return the mainLayout
        },
        update = { view ->
            // Update the view if needed (not required in this case)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MrTimePart2Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LoginPageLayout()
        }
    }
}