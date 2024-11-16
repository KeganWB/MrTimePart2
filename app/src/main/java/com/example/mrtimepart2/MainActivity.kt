package com.example.mrtimepart2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val toolbar = binding.toolbar

        // Martins special login code ( KEEP SAFE )
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
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.nav_categories -> {
                    startActivity(Intent(this, Category::class.java))
                }
                R.id.nav_timesheets -> {
                    if (currentUser != null) { // Sends current Firebase User to timesheet activity
                        val userId = currentUser.uid
                        val intent = Intent(this, TimeSheetActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    }
                }
                R.id.nav_goals -> {
                    startActivity(Intent(this, HoursActivity::class.java))
                }
                R.id.nav_graph -> {
                    if (currentUser != null) { // Sends current Firebase User to timesheet activity
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
