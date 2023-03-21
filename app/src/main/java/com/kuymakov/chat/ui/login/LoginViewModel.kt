package com.kuymakov.chat.ui.login

import androidx.lifecycle.ViewModel
import com.kuymakov.chat.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val signIn: LoginUseCase) : ViewModel() {

    suspend fun login(email: String, password: String) = signIn(email, password)

}