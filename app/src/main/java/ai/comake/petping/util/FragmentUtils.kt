
package ai.comake.petping.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun Fragment.showAllowingStateLoss(fragmentManager: FragmentManager, tag: String = "") {
    fragmentManager.beginTransaction().add(this,null).commitAllowingStateLoss()
}