package tech.codevil.tracne.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import tech.codevil.tracne.R
import tech.codevil.tracne.databinding.ActivityMainBinding
import tech.codevil.tracne.db.EntryDao
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var entryDao: EntryDao

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupBottomNav()
    }

    private fun setupBottomNav() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navHostController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.mainBottomNavView, navHostController)
        navHostController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.mainBottomNavView.visibility = when (destination.id) {
                R.id.homeFragment, R.id.statisticsFragment -> View.VISIBLE
                else -> View.GONE
            }
        }
    }
}