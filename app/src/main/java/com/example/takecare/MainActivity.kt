package com.example.takecare

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // Instantiate the navController using the NavHostFragment
        navController = navHostFragment.navController

        //Get the bottom navigation from the xml layout
        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        bottomNav?.setupWithNavController(navController)

        //Change the up button behavior to considerate the HomeFragment, DoctorsFragment
        //and AppointmentFragment  as top level destination
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.doctorsFragment, R.id.myAppointmentFragment
            )
        )
        // Make sure actions in the ActionBar get propagated
        setupActionBarWithNavController(navController, appBarConfiguration)
        configureBottomNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun configureBottomNavigation() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomNav()
                R.id.doctorsFragment -> showBottomNav()
                R.id.myAppointmentFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        bottomNav.visibility = View.VISIBLE

    }

    private fun hideBottomNav() {
        bottomNav.visibility = View.GONE

    }

    companion object {
        const val TAG = "MainActivity"
    }
}