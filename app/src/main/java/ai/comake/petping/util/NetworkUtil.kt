package ai.comake.petping.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.io.IOException

/**
 * android-petping-2
 * Class: NetworkUtil
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
object NetworkUtil {
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networks = cm.allNetworks
        var hasInternet = false
        if (networks.isNotEmpty()) {
            networks.forEach {
                val networkCapabilities = cm.getNetworkCapabilities(it)
                if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    hasInternet = true
                }
            }
        }
        return hasInternet
    }

    class NoConnectivityException: IOException() {
        override val message: String?
            get() = "No Internet"
    }
}