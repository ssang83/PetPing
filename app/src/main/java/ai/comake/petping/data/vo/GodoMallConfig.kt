package ai.comake.petping.data.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * android-petping-2
 * Class: GodoMallConfig
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
@Parcelize
data class GodoMallConfig(
    val url:String,
    val productUrl:String
) : Parcelable
