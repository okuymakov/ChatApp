package com.kuymakov.chat.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.kuymakov.chat.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val signUp: RegisterUseCase) : ViewModel() {

    suspend fun register(username: String, email: String, password: String) =
        signUp(username, email, password)


    fun verifyEmail(email: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun verifyPassword(password: String): Boolean {
        return password.length >= 6
    }
}
