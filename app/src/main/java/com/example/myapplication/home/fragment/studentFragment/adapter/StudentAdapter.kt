package com.example.myapplication.home.fragment.studentFragment.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.databinding.StudentCardLayoutBinding
import com.example.myapplication.home.fragment.studentFragment.StudentFragment
import com.example.myapplication.model.Student
import com.example.myapplication.util.AesService

class StudentAdapter(
    private val listener: StudentFragment
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var students: MutableList<Student> = mutableListOf()
    private val aesService: AesService = AesService()

    inner class StudentViewHolder(
        private val binding: StudentCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(student: Student) {
            binding.ivStudentProfile.load(aesService.decryptFieldData(student.details?.imageUrl.toString()))
            binding.tvStudentName.text = aesService.decryptFieldData(student.details?.username.toString())
            binding.tvStudentEmail.text = student.details?.email

            binding.cvStudent.setOnClickListener {
                listener.navigateToStudentView(student)
            }

            binding.ivDeleteStudent.setOnClickListener {
                listener.deleteStudent(student)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = StudentCardLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(student = students[position])
    }

    override fun getItemCount(): Int = students.size

    fun setData(newStudents: List<Student>) {
        students.clear()
        students.addAll(newStudents)
        notifyDataSetChanged()
    }

}