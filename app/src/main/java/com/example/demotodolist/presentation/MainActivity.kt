package com.example.demotodolist.presentation

import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.demotodolist.R
import com.example.demotodolist.base.Base_View.BaseActivity
import com.example.demotodolist.databinding.ActivityMainBinding
import com.example.demotodolist.presentation.di.DemoDoListApplication


class MainActivity : BaseActivity<ActivityMainBinding>() {
    // region
    val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.MainActivityViewModelFactory((application as DemoDoListApplication).repository)
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    //endregion

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initListener() {

    }

    override fun initData() {
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        if (!navController.popBackStack()) {
            val currentDestination = navController.currentDestination?.id
            when (currentDestination) {
                R.id.home_fragment -> {
                    super.onBackPressed()
                }
                R.id.settings_fragment -> {
                    navController.navigate(R.id.home_fragment)
                }
                R.id.completed_tasks_fragment -> {
                    navController.navigate(R.id.home_fragment)
                }
                else -> {
                    super.onBackPressed()
                }
            }
        }
    }


    override fun initView() {
        setSupportActionBar(binding.contentMain.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home_fragment, R.id.settings_fragment, R.id.completed_tasks_fragment
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}