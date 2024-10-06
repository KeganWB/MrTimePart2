package com.example.mrtimepart2

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mrtimepart2.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val toolbar = binding.toolbar

        //Martins special login code ( KEEP SAFE )
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("email", "") // Get the logged-in user's email


        // Set up the toolbar as ActionBar
        setSupportActionBar(toolbar)

        // Set up the ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,  // Pass the toolbar here
            R.string.navigation_drawer_open,  // Your open drawer string
            R.string.navigation_drawer_close  // Your close drawer string
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Enable the hamburger icon in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle Navigation Item Selection
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Navigate to Home
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.nav_categories -> {
                    // Navigate to Categories
                    startActivity(Intent(this, Category::class.java))
                }
                R.id.nav_timesheets -> {
                    // Navigate to Timesheets
                    startActivity(Intent(this, TimeSheetActivity::class.java))
                }
                R.id.nav_goals -> {
                    // Navigate to Goals
                    startActivity(Intent(this, HoursActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Override this method to handle the toggle action
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}



