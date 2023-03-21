package com.kuymakov.chat.ui.splash

import androidx.lifecycle.ViewModel
import com.kuymakov.chat.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authRepo: AuthRepository) : ViewModel() {

    fun userAuthorized(): Boolean {
        return authRepo.userAuthorized()
    }
}