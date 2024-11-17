package com.example.mrtimepart2

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth

class LoginPage : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 123
        const val CHANNEL_ID = "YOUR_CHANNEL_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channel
        createNotificationChannel()

        // Check and request permissions
        checkAndRequestPermissions()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Send notification on login page access
        sendNotification()

        // Setup alarm when login page is accessed
        setupAlarm()

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun shouldSendNotification(): Boolean {
        val sharedPref = getSharedPreferences("notification_pref", Context.MODE_PRIVATE)
        val hasSentNotification = sharedPref.getBoolean("notification_sent", false)
        if (!hasSentNotification) {
            with(sharedPref.edit()) {
                putBoolean("notification_sent", true)
                apply()
            }
            return true
        }
        return false
    }

    private fun sendNotification() {
        if (shouldSendNotification()) {
            Log.d("LoginPage", "sendNotification called")
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.mr_time)
                .setContentTitle("Welcome")
                .setContentText("Welcome To MrTime, Hope you enjoy your stay")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(this)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSIONS_REQUEST_CODE
                )
                return
            }
            notificationManager.notify(1, builder.build())
            Log.d("LoginPage", "Notification sent")
        } else {
            Log.d("LoginPage", "Notification already sent")
        }
    }

    private fun setupAlarm() {
        Log.d("LoginPage", "Setting up alarm")
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10, pendingIntent)
        Log.d("LoginPage", "Alarm set")
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
                })

                val signInText = TextView(ctx).apply {
                    text = "Sign In"
                    setTextColor(Color.parseColor("#e45a66"))
                    textSize = 24f
                }
                mainLayout.addView(signInText, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 20
                    gravity = Gravity.CENTER_HORIZONTAL
                })

                val welcomeText = TextView(ctx).apply {
                    text = "Hi there! Nice to see you again"
                    setTextColor(Color.parseColor("#e45a66"))
                    textSize = 16f
                }
                mainLayout.addView(welcomeText, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
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
                })

                val editTextPassword = EditText(ctx).apply {
                    hint = "Password"
                    setHintTextColor(Color.GRAY)
                    setTextColor(Color.parseColor("#e45a66"))
                    inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                mainLayout.addView(editTextPassword, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 20
                })

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
                mainLayout.addView(buttonSignIn, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 20
                })

                val buttonCreateAccount = Button(ctx).apply {
                    text = "Create Account"
                    setBackgroundColor(Color.parseColor("#e45a66"))
                    setTextColor(Color.parseColor("#FFFFFFFF"))
                    setOnClickListener {
                        ctx.startActivity(Intent(ctx, CreateAccountActivity::class.java))
                    }
                }
                mainLayout.addView(buttonCreateAccount, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 10
                })

                mainLayout
            },
            update = {}
        )
    }
}
