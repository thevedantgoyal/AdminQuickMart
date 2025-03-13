package com.example.adminquickmart.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.adminquickmart.R
import com.example.adminquickmart.databinding.ActivityAdminMainBinding


class AdminMainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navController = findNavController(R.id.fragmentContainerView2)
        setupWithNavController(binding.bottomNavBar , navController)

    }
}