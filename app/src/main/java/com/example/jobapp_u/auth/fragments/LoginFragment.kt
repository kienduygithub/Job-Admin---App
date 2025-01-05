package com.example.jobapp_u.auth.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.jobapp_u.R
import com.example.jobapp_u.auth.viewmodel.AuthViewModel
import com.example.jobapp_u.databinding.FragmentLoginBinding
import com.example.jobapp_u.home.activity.HomeActivity
import com.example.jobapp_u.user_detail.UserDetailActivity
import com.example.jobapp_u.util.Constants.Companion.ROLE_TYPE_STUDENT
import com.example.jobapp_u.util.InputValidation
import com.example.jobapp_u.util.LoadingDialog
import com.example.jobapp_u.util.Status
import com.example.jobapp_u.util.addTextWatcher
import com.example.jobapp_u.util.clearText
import com.example.jobapp_u.util.getInputValue
import com.example.jobapp_u.util.showToast
import java.util.concurrent.Executor

class LoginFragment : Fragment() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var sharedPreferences: SharedPreferences? = null

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val authViewModel by viewModels<AuthViewModel>()
    private var emailText = ""
    private var passwordText = ""

    companion object {
        private const val REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("data", android.content.Context.MODE_PRIVATE)

        setupBiometricAuthentication()
        setupUI()
        setupObserver()

        checkLoginStatus()

        return binding.root
    }

    private fun checkLoginStatus() {
        sharedPreferences?.let {
            val isLogin: Boolean = it.getBoolean("isLogin", false)
            if (isLogin) {
                binding.imageViewLogin.visibility = View.VISIBLE
            }
        }
    }

    private fun setupBiometricAuthentication() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Log.e("MY_APP_TAG", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                startActivityForResult(enrollIntent, REQUEST_CODE)
            }
        }

        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(), "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    sharedPreferences?.let {
                        val email = it.getString("email", "")
                        val password = it.getString("password", "")
                        if(detailVerification(email, password)){
                            authViewModel.login(email!!, password!!)
                            clearField()
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        binding.imageViewLogin.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun setupUI() {
        binding.apply {
            tvSignup.text = createSignupText()

            tvSignup.setOnClickListener {
                navigateToSignup()
            }

            tvForgetPassword.setOnClickListener {
                navigateToForgotPassword()
            }

            etEmailContainer.addTextWatcher()
            etPasswordContainer.addTextWatcher()

            btnLogin.setOnClickListener {
                val email = etEmail.getInputValue()
                val password = etPassword.getInputValue()
                if (detailVerification(email, password)) {
                    authViewModel.login(email, password)
                    emailText = email
                    passwordText = password
                }
            }
        }
    }

    private fun performAuth(email: String, password: String){
        val editor :SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.commit()
    }

    private fun setupObserver() {
        authViewModel.loginStatus.observe(viewLifecycleOwner) { loginState ->
            when (loginState.status) {
                Status.LOADING -> loadingDialog.show()
                Status.SUCCESS -> {
                    val currentUser = loginState.data!!
                    if (currentUser.roleType == ROLE_TYPE_STUDENT) {
                        if (currentUser.userInfoExist) {
                            navigateToHomeActivity()
                            if(emailText == "" || passwordText == ""){
                                sharedPreferences?.let {
                                    val email = it.getString("email", "")
                                    val password = it.getString("password", "")
                                    if(detailVerification(email, password)){
                                        performAuth(email!!, password!!)
                                    }
                                }
                            }else{
                                performAuth(emailText, passwordText)
                            }

                        } else {
                            navigateToUserDetail(currentUser.username, currentUser.email)
                        }
                        showToast(requireContext(), getString(R.string.auth_pass))
                    } else {
                        showToast(requireContext(), "Account doesn't exist")
                    }
                    loadingDialog.dismiss()
                }
                Status.ERROR -> {
                    showToast(requireContext(), loginState.message.toString())
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun createSignupText(): SpannableString {
        val signupText = SpannableString(getString(R.string.sign_up_prompt))
        val color = ContextCompat.getColor(requireActivity(), R.color.on_boarding_span_text_color)
        val signupColor = ForegroundColorSpan(color)
        signupText.setSpan(UnderlineSpan(), 10, signupText.length, 0)
        signupText.setSpan(signupColor, 10, signupText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        return signupText
    }

    private fun clearField() {
        binding.etEmail.clearText()
        binding.etPassword.clearText()
    }

    private fun navigateToHomeActivity() {
        val homeActivity = Intent(requireContext(), HomeActivity::class.java)
        startActivity(homeActivity)
        requireActivity().finish()
    }

    private fun navigateToUserDetail(username: String, email: String) {
        val userDetailActivity = Intent(requireContext(), UserDetailActivity::class.java)
        userDetailActivity.putExtra("USERNAME", username)
        userDetailActivity.putExtra("EMAIL", email)
        requireActivity().startActivity(userDetailActivity)
        requireActivity().finish()
    }

    private fun navigateToForgotPassword() {
        findNavController().navigate(R.id.action_loginFragment_to_forgotPassFragment)
    }

    private fun navigateToSignup() {
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }

    private fun detailVerification(email: String?, password: String?): Boolean {
        binding.apply {
            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                showToast(requireContext(), "Email or password cannot be empty")
                return false
            }
            val (isEmailValid, emailError) = InputValidation.isEmailValid(email)
            if (!isEmailValid) {
                etEmailContainer.error = emailError
                return false
            }

            val (isPasswordValid, passwordError) = InputValidation.isPasswordValid(password)
            if (!isPasswordValid) {
                etPasswordContainer.error = passwordError
                return false
            }
            return true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
