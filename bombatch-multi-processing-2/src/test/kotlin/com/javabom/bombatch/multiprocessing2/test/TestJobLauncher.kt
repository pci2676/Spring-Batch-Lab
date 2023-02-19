package com.javabom.bombatch.multiprocessing2.test

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.context.ApplicationContext

class TestJobLauncher(
    private val applicationContext: ApplicationContext,
    private val jobRepository: JobRepository,
    private val jobLauncher: JobLauncher
) {
    fun launchJob(jobName: String, jobParameters: JobParameters): JobExecution {
        val job = applicationContext.getBean(jobName, Job::class.java)
        return getJobLauncherTestUtils(job).launchJob(jobParameters)
    }

    private fun getJobLauncherTestUtils(job: Job): JobLauncherTestUtils {
        val jobLauncherTestUtils = JobLauncherTestUtils()
        jobLauncherTestUtils.jobLauncher = jobLauncher
        jobLauncherTestUtils.jobRepository = jobRepository
        jobLauncherTestUtils.job = job
        return jobLauncherTestUtils
    }
}
