package com.kuymakov.chat.base.ui

import android.graphics.Color
import android.os.Build
import androidx.annotation.LayoutRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment


abstract class FullscreenFragment(@LayoutRes layout: Int) : Fragment(layout) {
    private var  prevStatusBarColor: Int? = null
    private var  prevNavigationBarColor: Int? = null

    override fun onStart() {
        super.onStart()
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, requireView()).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    isNavigationBarContrastEnforced = false
                }
                isAppearanceLightNavigationBars = false
                hide(WindowInsetsCompat.Type.statusBars())
                prevStatusBarColor = statusBarColor
                prevNavigationBarColor = navigationBarColor
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = Color.TRANSPARENT
            }
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, requireView()).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    isNavigationBarContrastEnforced = true
                }
                isAppearanceLightNavigationBars = true
                show(WindowInsetsCompat.Type.statusBars())
                statusBarColor = prevStatusBarColor!!
                navigationBarColor = prevNavigationBarColor!!
            }
        }
    }
}