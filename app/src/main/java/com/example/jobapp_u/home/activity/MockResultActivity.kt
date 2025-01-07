package com.example.jobapp_u.home.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobapp_u.R
import com.example.jobapp_u.databinding.ActivityMockResultBinding
import com.example.jobapp_u.home.adapter.MockSolutionAdapter
import com.example.jobapp_u.home.viewmodel.MockSolutionViewModel
import com.example.jobapp_u.util.LoadingDialog
import com.example.jobapp_u.util.Status
import com.example.jobapp_u.util.checkTimeUnit
import com.example.jobapp_u.util.showToast

class MockResultActivity : AppCompatActivity() {
    private var _binding: ActivityMockResultBinding? = null
    private val binding get() = _binding!!
    private var _mockSolutionAdapter: MockSolutionAdapter? = null
    private val mockSolutionAdapter get() = _mockSolutionAdapter!!
    private val mockSolutionViewModel by viewModels<MockSolutionViewModel>()
    private val loadingDialog by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMockResultBinding.inflate(layoutInflater)
        _mockSolutionAdapter = MockSolutionAdapter()
        setContentView(binding.root)
        val mockId = intent.extras?.getString("MOCK_ID")!!
        mockSolutionViewModel.fetchMockResult(mockId)

        setupUI()
        setupObserver()
    }

    private fun setupUI() {

        binding.ivPopOut.setOnClickListener {
            finish()
        }

        binding.rvSolution.adapter = mockSolutionAdapter
        binding.rvSolution.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObserver() {
        mockSolutionViewModel.mockResultState.observe(this) { mockResultState ->
            when (mockResultState.status) {
                Status.LOADING -> {
                    loadingDialog.show()
                }
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    val mockSolutionState = mockResultState.data
                    if (mockSolutionState != null) {
                        val totalQuestion = mockSolutionState.mockResult.totalQuestion.toFloat()
                        val correctAns = mockSolutionState.mockResult.correctAns.toInt()
                        val progress = (correctAns / totalQuestion) * 100
                        binding.scoreProgressBar.progress = progress.toInt()
                        binding.tvScore.text = getString(
                            R.string.field_score,
                            mockSolutionState.mockResult.correctAns,
                            mockSolutionState.mockResult.totalQuestion
                        )
                        binding.tvIncorrectScore.text = getString(
                            R.string.field_score,
                            mockSolutionState.mockResult.incorrectAns,
                            mockSolutionState.mockResult.totalQuestion
                        )
                        binding.tvUnAttemptedScore.text = getString(
                            R.string.field_score,
                            mockSolutionState.mockResult.unAttempted,
                            mockSolutionState.mockResult.totalQuestion
                        )
                        binding.tvTimeTakenScore.text = checkTimeUnit(mockSolutionState.mockResult.timeTaken)
                        mockSolutionAdapter.setMockQuestions(mockSolutionState.mockQuestions)
                    }
                }
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    val errorMessage = mockResultState.message!!
                    showToast(this, errorMessage)
                }
            }

        }
    }

    override fun onDestroy() {
        _mockSolutionAdapter = null
        _binding = null
        super.onDestroy()
    }
}