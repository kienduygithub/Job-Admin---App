package com.example.jobapp_u.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobapp_u.databinding.MockTestCardLayoutBinding
import com.example.jobapp_u.home.fragments.MockTestFragment
import com.example.jobapp_u.model.MockTestState

class MockTestAdapter(private val listener: MockTestFragment) :
    RecyclerView.Adapter<MockTestAdapter.MockTestViewHolder>() {

    private val mockTestState: MutableList<MockTestState> = mutableListOf()

    inner class MockTestViewHolder(
        private val binding: MockTestCardLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mockTestState: MockTestState){
            binding.apply {
                tvMockTestName.text = mockTestState.quizName
                if (mockTestState.hasAttempted) {
                    tvMockTestAttemptStatus.text = "Already Attempted."
                } else {
                    tvMockTestAttemptStatus.text = "Not Attempted."
                }
                cvMockTest.setOnClickListener {
                    cvMockTest.isEnabled = false
                    if (mockTestState.hasAttempted){
                        listener.navigateToMockResult(mockTestState.quizUid)
                    } else {
                        listener.navigateToMockQuestion(mockTestState.quizUid)
                    }
                    cvMockTest.isEnabled = true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MockTestViewHolder {
        return MockTestViewHolder(
            MockTestCardLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MockTestViewHolder, position: Int) {
        holder.bind(mockTestState[position])
    }

    override fun getItemCount(): Int = mockTestState.size

    fun setMockState(newQuizDetail: List<MockTestState>) {
        mockTestState.clear()
        mockTestState.addAll(newQuizDetail)
        notifyDataSetChanged()
    }

}