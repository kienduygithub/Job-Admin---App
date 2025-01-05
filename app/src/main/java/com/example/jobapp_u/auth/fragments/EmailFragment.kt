package com.example.jobapp_u.auth.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.jobapp_u.R
import com.example.jobapp_u.auth.viewmodel.AuthViewModel
import com.example.jobapp_u.databinding.FragmentEmailBinding
import com.example.jobapp_u.util.LoadingDialog
import com.example.jobapp_u.util.MailService
import java.util.Random

class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!
    private val mailService: MailService = MailService()

    private fun showKeyboard(optET: EditText) {
        optET.requestFocus()
        val inputMethodManager: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(optET, InputMethodManager.SHOW_IMPLICIT)
    }
    private val authViewModel by viewModels<AuthViewModel>()
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEmailBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {

            var selectedPosition: Int = 0;
            val email = arguments?.getString("email")
            var otp = arguments?.getString("otp")
            var optET1: EditText = binding.otpET1
            var optET2: EditText = binding.otpET2
            var optET3: EditText = binding.otpET3
            var optET4: EditText = binding.otpET4
            val resendOTP: TextView = binding.tvResendOTP
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Không cần xử lý
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Không cần xử lý
                }

                override fun afterTextChanged(s: Editable?) {
                    if ((s?.length ?: 0) > 0) {
                        when (selectedPosition) {
                            0 -> {
                                selectedPosition = 1
                                showKeyboard(optET2)
                            }
                            1 -> {
                                selectedPosition = 2
                                showKeyboard(optET3)
                            }
                            2 -> {
                                selectedPosition = 3
                                showKeyboard(optET4)
                            }
                        }
                    }
                }
            }

            val tvEmailSubHeading = binding.tvEmailSubHeading;
            tvEmailSubHeading.text = "Mã xác thực OTP đã được gửi đến địa chỉ email $email. Vui lòng nhập mã xác thực để đặt lại mật khẩu cho tài khoản của bạn"

            optET1.addTextChangedListener(textWatcher)
            optET2.addTextChangedListener(textWatcher)
            optET3.addTextChangedListener(textWatcher)
            optET4.addTextChangedListener(textWatcher)
            showKeyboard(optET1)

            btnBackToLogin.setOnClickListener {
                findNavController().popBackStack(R.id.loginFragment, false)
            }

            btnOpenEmail.setOnClickListener {
                val generateOTP = "${optET1.text}${optET2.text}${optET3.text}${optET4.text}"

                if(generateOTP == otp.toString()){
                    authViewModel.resendPassword(email.toString());
                    Toast.makeText(requireContext(), "Chính xác. Di chuyển đển tài khoản mail để đổi mật khẩu mới", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack(R.id.loginFragment, false)
                }else{
                    Toast.makeText(requireContext(), "Mã xác thực không chính xác", Toast.LENGTH_SHORT).show()
                }
            }

            resendOTP.setOnClickListener{
                randomOtp();
                mailService.sendEmail(
                    "Đặt lại mật khẩu",
                    "Mã OTP của bạn là: $_random",
                    email.toString()
                )
                otp = _random.toString()
            }
        }
    }

    var _random: String = ""
    private fun randomOtp(){
        val random: Random = Random();
        val randomNumber: Int = 1000 + random.nextInt(9999);
        _random = randomNumber.toString();
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}