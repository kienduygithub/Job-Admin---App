package com.example.myapplication.home.fragment.jobsFragment.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.databinding.FragmentStudentBinding
import com.example.myapplication.databinding.PendingStudentsCardLayoutBinding
import com.example.myapplication.home.fragment.jobsFragment.StudentJobFragment
import com.example.myapplication.home.fragment.studentFragment.viewModel.StudentViewModel
import com.example.myapplication.model.JobApplication
import com.example.myapplication.model.JobStatus
import com.example.myapplication.util.AesService
import com.example.myapplication.util.LoadingDialog

class PendingStudentAdapter(
    private val setJobStatus: (JobApplication) -> Unit,
    private val listener: StudentJobFragment
) : RecyclerView.Adapter<PendingStudentAdapter.PendingStudentViewHolder>() {

    private val pendingStudents = mutableListOf<JobStatus>()
    private val aesService: AesService = AesService()

    inner class PendingStudentViewHolder(
        private val binding: PendingStudentsCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(jobStatus: JobStatus) {
            with(binding) {
                val student = jobStatus.student
                val details = student.details!!
                ivStudentProfile.load(aesService.decryptFieldData(details.imageUrl))
                tvStudentName.text = aesService.decryptFieldData(details.username)
                tvStudentEmail.text = details.email
                ivAccepted.setOnClickListener {
                    jobStatus.jobApplication.applicationStatus = "Accepted"
                    jobStatus.jobApplication.isEvaluated = true
                    setJobStatus(jobStatus.jobApplication)
                }
                ivRejected.setOnClickListener {
                    jobStatus.jobApplication.applicationStatus = "Declined"
                    jobStatus.jobApplication.isEvaluated = true
                    setJobStatus(jobStatus.jobApplication)
                }

                binding.ivStudentProfile.setOnClickListener {
                    listener.navigateToStudentView(student)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingStudentViewHolder {
        return PendingStudentViewHolder(
            PendingStudentsCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PendingStudentViewHolder, position: Int) {
        holder.bind(jobStatus = pendingStudents[position])
    }

    override fun getItemCount(): Int = pendingStudents.size

    fun setPendingStudent(newPendingStudent: List<JobStatus>) {
        pendingStudents.clear()
        pendingStudents.addAll(newPendingStudent)
        notifyDataSetChanged()
    }
}