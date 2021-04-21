package com.peluso.walletguru

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.peluso.walletguru.viewmodel.MainViewModel

const val LOCATION_PREF_KEY = "locationPrefs"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        setSupportActionBar(findViewById(R.id.toolbar))

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.navigation_dashboard, R.id.navigation_home, R.id.navigation_notifications
                )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // immediately after creating the viewmodel we set up the viewmodel to use db
        val db = (application as MainApplication).getDb()
        viewModel.setDatabase(db.accountsDao(), db.favoritesDao())
        // this allows us to add values to shared prefs from the viewmodel
        viewModel.setSharedPrefPutter {
            with(getPreferences(Context.MODE_PRIVATE).edit()) {
                putString(LOCATION_PREF_KEY, it)
                apply()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.action_favorites -> {
                    navController.navigate(R.id.favorites_fragment)
                    true
                }
                R.id.action_about -> {
                    navController.navigate(R.id.about_fragment)
                    true
                }
                android.R.id.home -> {
                    navController.navigateUp()
                    true
                }
                R.id.action_settings -> {
                    navController.navigate(R.id.settings_fragment)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

}