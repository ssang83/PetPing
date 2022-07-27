package ai.comake.petping.ui.home

import ai.comake.petping.R
import ai.comake.petping.ui.home.insurance.InsuranceFragment
import ai.comake.petping.ui.home.reward.RewardFragment
import ai.comake.petping.ui.home.shop.ShopFragment
import ai.comake.petping.ui.home.walk.WalkFragment

enum class HomeTab(
    val itemId: Int,
    val tag: String
) {
    DASHBOARDSCREEN(R.id.dashBoardScreen, HomeFragment.TAG),
    WALKSCREEN(R.id.walkScreen, WalkFragment.TAG),
    REWARDSCREEN(R.id.rewardScreen, RewardFragment.TAG),
    SHOPSCREEN(R.id.shopScreen, ShopFragment.TAG),
    INSURANCEFRAGMENT(R.id.insuranceScreen, InsuranceFragment.TAG);

    companion object {
        fun from(itemId: Int): HomeTab? = values().firstOrNull { it.itemId == itemId }
    }
}

fun HomeTab.Companion.otherTab(exceptTag: String): Sequence<HomeTab> =
    HomeTab.values()
        .asSequence()
        .filter {
            it.tag != exceptTag
        }