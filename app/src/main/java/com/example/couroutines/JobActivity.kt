package com.example.couroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import com.example.couroutines.databinding.ActivityJobBinding
import kotlinx.coroutines.*

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class JobActivity : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000
    private lateinit var job: CompletableJob


    private lateinit var binding: ActivityJobBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.jobBtn.setOnClickListener {
            if (!::job.isInitialized)
                initJob()
            binding.progress.startJobOrCancel(job)
        }

    }

    private fun initJob() {
        binding.jobBtn.text = "Start job #1"
        binding.jobCompleteText.text = ""
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank())
                    msg = "Unknown cancellation error"

                println("$job was cancelled .Reason : $msg")
            }
        }
        binding.progress.max = PROGRESS_MAX
        binding.progress.progress = PROGRESS_START

    }

    fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            resetJob()
        } else {
            binding.jobBtn.text = "Cancel job #1"
            //tamam coroutin haye IO ro cancel mikone az background - raveshe bad
//            val scope = CoroutineScope(IO).launch {
//
//            }
//            scope.cancel()

            //barayr cancel kardan hamon job
            CoroutineScope(IO + job).launch {
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is complete")
            }

        }

    }

    private fun updateJobCompleteTextView(text: String) {
        GlobalScope.launch(Main) {
            binding.jobCompleteText.text = text
        }
        initJob()
    }

    private fun resetJob() {

        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job"))
            updateJobCompleteTextView("Start job #1")
        }

    }
}