package com.example.myapplication.home.fragment.jobsFragment.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.databinding.EvaluatedStudentCardLayoutBinding
import com.example.myapplication.home.fragment.jobsFragment.StudentJobFragment
import com.example.myapplication.model.JobStatus
import com.example.myapplication.util.AesService

class EvaluationStudentAdapter(
    private val listener: StudentJobFragment
) : RecyclerView.Adapter<EvaluationStudentAdapter.EvaluationStudentViewHolder>() {

    private val evaluatedStudent = mutableListOf<JobStatus>()
    private val aesService: AesService = AesService()

    inner class EvaluationStudentViewHolder(
        private val binding: EvaluatedStudentCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(jobStatus: JobStatus) {
            val student = jobStatus.student
            val details = student.details!!
            with(binding) {
                ivStudentProfile.load(aesService.decryptFieldData(details.imageUrl))
                tvStudentName.text = aesService.decryptFieldData(details.username)
                tvStudentEmail.text = details.email
                tvApplicantResult.text = jobStatus.jobApplication.applicationStatus

                binding.ivStudentProfile.setOnClickListener {
                    listener.navigateToStudentView(student)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluationStudentViewHolder {
        return EvaluationStudentViewHolder(
            EvaluatedStudentCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EvaluationStudentViewHolder, position: Int) {
        holder.bind(evaluatedStudent[position])
    }

    override fun getItemCount(): Int = evaluatedStudent.size

    fun setEvaluatedStudent(newEvaluatedStudents: List<JobStatus>) {
        evaluatedStudent.clear()
        evaluatedStudent.addAll(newEvaluatedStudents)
        notifyDataSetChanged()
    }
}