package com.example.mrtimepart2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mrtimepart2.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private val currentUser = FirebaseAuth.getInstance().currentUser

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 123
        const val CHANNEL_ID = "YOUR_CHANNEL_ID"
    }

    private var isNavigatingToOtherActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MainActivity", "onCreate called")

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val toolbar = binding.toolbar

        //Martins special login code ( KEEP SAFE )
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("email", "")

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            isNavigatingToOtherActivity = true
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.nav_categories -> {
                    startActivity(Intent(this, Category::class.java))
                }
                R.id.nav_timesheets -> {
                    if(currentUser != null) {
                        val userId = currentUser.uid
                        val intent = Intent(this, TimeSheetActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    }
                }
                R.id.nav_goals -> {
                    if(currentUser != null) {
                        val userId = currentUser.uid
                        val intent = Intent(this, HoursActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    }
                }
                R.id.nav_graph -> {
                    if (currentUser != null) { //Sends current Firebase User to timesheet activity
                        val userId = currentUser.uid
                        val intent = Intent(this, GraphActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    }
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isNavigatingToOtherActivity) {
            sendGoodbyeNotification()
        }
    }

    override fun onStop() {
        super.onStop()
        isNavigatingToOtherActivity = false // Reset flag
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                val perms = mutableMapOf<String, Int>()
                for (i in permissions.indices) {
                    perms[permissions[i]] = grantResults[i]
                }
                if (perms[Manifest.permission.POST_NOTIFICATIONS] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Notification permission granted")
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendGoodbyeNotification() {
        Log.d("MainActivity", "sendGoodbyeNotification called")
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.mr_time)
            .setContentTitle("Goodbye")
            .setContentText("Sorry to see you go, have you completed everything you wanted to do?")
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
        notificationManager.notify(2, builder.build())
        Log.d("MainActivity", "Goodbye notification sent")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
