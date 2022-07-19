package ai.comake.petping.ui.history.walk

import ai.comake.petping.data.vo.WalkStats
import ai.comake.petping.databinding.FragmentWeeklyReportBinding
import ai.comake.petping.ui.base.BaseFragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * android-petping-2
 * Class: WeeklyReportFragment
 * Created by cliff on 2022/03/02.
 *
 * Description:
 */
class WeeklyReportFragment(
    private val weeklyReport: WalkStats? = null
) : BaseFragment<FragmentWeeklyReportBinding>(FragmentWeeklyReportBinding::inflate) {

    @SuppressLint("CutPasteId", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            try {
                if (weeklyReport?.walkDayCount == 0) {
                    dateCount.visibility = View.GONE
                    day.visibility = View.GONE
                    noHistory.visibility = View.VISIBLE
                } else {
                    dateCount.visibility = View.VISIBLE
                    day.visibility = View.VISIBLE
                    noHistory.visibility = View.GONE
                    dateCount.text = weeklyReport?.walkDayCount.toString()
                }

                targetDate.text = "${stringToDate(weeklyReport?.startDate ?: "")} - ${stringToDate(weeklyReport?.endDate ?: "")}"
                time.text = weeklyReport?.time
                count.text = weeklyReport?.count
                distance.text = weeklyReport?.distance
                marking.text = weeklyReport?.markingCount
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    private fun stringToDate(date: String): String {
        val dates = date.split("-")
        return if (dates.isEmpty()) ""
        else "${dates[dates.size - 2]}월 ${dates[dates.size - 1]}일"
    }
}