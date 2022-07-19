package ai.comake.petping.firebase

import ai.comake.petping.util.LogUtil
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class IJobWorker (appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): ListenableWorker.Result {
        LogUtil.log(TAG, "Performing long running task in scheduled job")
        return Result.success()
    }

    companion object {
        private val TAG = "PetPingJobWorker"
    }
}