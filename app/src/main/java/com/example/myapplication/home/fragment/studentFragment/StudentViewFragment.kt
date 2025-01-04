package com.example.myapplication.home.fragment.studentFragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentStudentViewBinding
import com.example.myapplication.util.AesService


class StudentViewFragment : Fragment() {
    private var _binding : FragmentStudentViewBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<StudentViewFragmentArgs>()
    private val aesService: AesService = AesService()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentViewBinding.inflate(inflater, container, false)

        setupUI()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI() {
        binding.apply {

            ivPopOut.setOnClickListener {
                findNavController().popBackStack()
            }

            tvUsername.text = aesService.decryptFieldData(args.student.details?.username.toString())
            tvUserEmail.text = args.student.details?.email
            profileImage.load(aesService.decryptFieldData(args.student.details?.imageUrl.toString()))
            tvSapId.text = aesService.decryptFieldData(args.student.details?.sapId.toString())
            tvMobile.text = aesService.decryptFieldData(args.student.details?.mobile.toString())
            tvDob.text = aesService.decryptFieldData(args.student.details?.dob.toString())
            tvAvgSGPI.text = aesService.decryptFieldData(args.student.academic?.avgScore.toString())
            tvAddress.text = getString(
                R.string.student_address,
                aesService.decryptFieldData(args.student.address?.address.toString()),
                aesService.decryptFieldData(args.student.address?.city.toString()),
                aesService.decryptFieldData(args.student.address?.zipCode.toString())
            )
            layoutUploadedPdf.tvFileName.text = args.fileName
            layoutUploadedPdf.tvFileMetaData.text = args.fileMetaData

            layoutUploadedPdf.root.setOnClickListener {
                setPdfIntent(Uri.parse(args.student.academic?.resumeUrl))
            }
        }
    }

    private fun setPdfIntent(pdfUri: Uri) {
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfUri, "application/pdf")
        startActivity(pdfIntent)
    }
}