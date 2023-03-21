package com.kuymakov.chat.ui.splash

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kuymakov.chat.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    private val navController: NavController by lazy { findNavController() }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context =
            ContextThemeWrapper(requireContext(), R.style.Theme_Chat_Splash)
        return inflater.cloneInContext(contextThemeWrapper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            if (viewModel.userAuthorized()) {
                navController.navigate(
                    SplashFragmentDirections.actionSplashFragmentToChatFragment()
                )
            } else {
                navController.navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }
    }
}