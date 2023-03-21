package com.kuymakov.chat.ui.register

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.showToast
import com.kuymakov.chat.base.ui.viewBinding
import com.kuymakov.chat.base.network.Failure
import com.kuymakov.chat.base.network.RegisterFailure
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val viewModel: RegisterViewModel by viewModels()
    private val navController: NavController by lazy { findNavController() }
    private val binding by viewBinding(FragmentRegisterBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    private fun bind() {
        binding.username.addTextChangedListener {
            if (binding.email.text?.isNotBlank() == true) {
                binding.usernameTil.onSuccess()
            }
        }

        binding.email.addTextChangedListener {
            if (viewModel.verifyEmail(binding.email.text.toString())) {
                binding.emailTil.onSuccess()
            } else {
                binding.emailTil.error =
                    resources.getString(R.string.input_validation_error_email)
            }
        }

        binding.password.addTextChangedListener {
            if (viewModel.verifyPassword(binding.password.text.toString())) {
                binding.passwordTil.onSuccess()
            } else {
                binding.passwordTil.error =
                    resources.getString(R.string.input_validation_error_password)
            }
        }

        binding.registerButton.setOnClickListener {
            with(binding) {
                lifecycleScope.launch {
                    val res = viewModel.register(
                        username.text.toString(),
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
                        is RegisterFailure.UserAlreadyExist -> {
                            requireContext().showToast(R.string.register_collision_error)
                        }
                        else -> {
                            requireContext().showToast(R.string.unknown_error)
                        }
                    }
                }
            }
        }
        binding.toLogin.setOnClickListener { navigateToLogin() }

    }

    private fun navigateToChat() {
        navController.navigate(RegisterFragmentDirections.actionRegisterFragmentToChatFragment())
    }

    private fun navigateToLogin() {
        navController.navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
    }

    fun TextInputLayout.onSuccess() {
        isErrorEnabled = false
        endIconMode = TextInputLayout.END_ICON_CUSTOM
        setEndIconDrawable(R.drawable.ic_done_24)
        boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.green_500)
    }

}
