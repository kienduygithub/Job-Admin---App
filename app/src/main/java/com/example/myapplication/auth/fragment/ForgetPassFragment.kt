package com.example.myapplication.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.auth.viewmodel.AuthViewModel
import com.example.myapplication.databinding.FragmentForgetPassBinding
import com.example.myapplication.util.*
import com.example.myapplication.util.Status.*
import java.util.Random

private const val TAG = "FORGOT_PASSWORD"

class ForgetPassFragment : Fragment() {
    private var _binding: FragmentForgetPassBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val mailService: MailService = MailService();
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetPassBinding.inflate(inflater, container, false)

        setupUI()
        setupObserver()

        return binding.root
    }

    private fun setupUI() {
        with(binding) {
            btnBackToLogin.setOnClickListener {
                findNavController().popBackStack()
            }

            etEmailContainer.addTextWatcher()

            btnResetPassword.setOnClickListener {
                val email = etEmail.getInputValue()
                val (isEmailValid, emailError) = InputValidation.isEmailValid(email)
                if (!isEmailValid.not()) {
                    randomOtp();
                    mailService.sendEmail(
                        "Đặt lại mật khẩu",
                        "Mã OTP của bạn là: $_random",
                        email
                    )

                    val bundle = Bundle().apply {
                        putString("email", email)
                        putString("otp", _random) // Truyền mã OTP
                    }

                    findNavController().navigate(R.id.action_forgetPassFragment_to_emailFragment, bundle)
                } else {
                    etEmailContainer.error = emailError
                }
            }
        }
    }

    var _random: String = ""
    private fun randomOtp(){
        val random: Random = Random();
        val randomNumber: Int = 1000 + random.nextInt(9000);
        _random = randomNumber.toString();
    }

    private fun setupObserver() {
        authViewModel.resendPasswordStatus.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                LOADING -> {
                    loadingDialog.show()
                }
                SUCCESS -> {
                    val successMessage = resource.data!!
                    showToast(requireContext(), successMessage)
                }
                ERROR -> {
                    val errorMessage = resource.message!!
                    showToast(requireContext(), errorMessage)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}