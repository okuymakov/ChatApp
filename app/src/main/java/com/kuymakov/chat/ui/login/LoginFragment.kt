package com.kuymakov.chat.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.showToast
import com.kuymakov.chat.base.ui.viewBinding
import com.kuymakov.chat.base.network.Failure
import com.kuymakov.chat.base.network.LoginFailure
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val viewModel: LoginViewModel by viewModels()
    private val navController: NavController by lazy { findNavController() }
    private val binding by viewBinding(FragmentLoginBinding::bind)

    private val inputs by lazy {
        mutableListOf(binding.email, binding.password)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        inputs.clear()
    }

    private fun bind() {
        inputs.forEach { input ->
            input.addTextChangedListener {
                binding.loginButton.isEnabled = !inputs.any { it.text?.isBlank() ?: true }
            }
        }

        binding.loginButton.setOnClickListener {
            with(binding) {
                lifecycleScope.launch {
                    val res = viewModel.login(
                        email.text.toString(),
                        password.text.toString()
                    )
                    when (res) {
                        is Success -> {
                            navigateToChat()
                        }
                        is Failure.NoInternetError -> {
                            requireContext().showToast(R.string.no_internet_error)
                        }
                        is Failure.TooManyRequestsError -> {
                            requireContext().showToast(R.string.too_many_requests_error)
                        }
                        is LoginFailure.IncorrectEmailOrPassword -> {
                            requireContext().showToast(R.string.login_incorrect_email_or_password_error)
                        }
                        else -> {
                            requireContext().showToast(R.string.unknown_error)
                        }
                    }
                }
            }
        }

        binding.toRegister.setOnClickListener { navigateToRegister() }
    }

    private fun navigateToChat() {
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToChatFragment())
    }

    private fun navigateToRegister() {
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
    }
}