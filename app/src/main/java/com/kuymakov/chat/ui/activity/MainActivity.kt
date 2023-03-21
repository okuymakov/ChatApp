package com.kuymakov.chat.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kuymakov.chat.R
import com.kuymakov.chat.data.UserPresenceHandler
import com.kuymakov.chat.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var userPresenceHandler: UserPresenceHandler
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Chat)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPresenceHandler.init()
        WindowInsetsControllerCompat(window, binding.root).apply {
            isAppearanceLightNavigationBars = true
        }
        window.apply {
            statusBarColor = ContextCompat.getColor(context, R.color.blue)
            navigationBarColor = ContextCompat.getColor(context, R.color.white_50)
        }
    }

}