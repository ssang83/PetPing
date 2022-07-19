package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: Ping
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
data class AvailablePings(
    val availablePings: Number,
    val savePingURL: String,
    val pingShopURL: String,
    val memberId: Number
)

data class DetailPings(
    val totalSave: Number,
    val totalUse: Number,
    val expireSoon: Number,
    val expire: Number
)

data class SavingPings(
    val memberId: String,
    val pageNo: Number,
    val nomOfRows: Number,
    val listSize: Number,
    val historyPings: List<SavingHistory>
)

data class SavingHistory(
    val type: Int,
    val typeStr: String,
    val historyType: Number,
    val reward: String,
    val targetName: String,
    val description: String,
    val saveDate: String,
    val useStartDate: String,
    val useEndDate: String,
    val isExpireSoon: Boolean
)

data class UsingPings(
    val memberId: String,
    val pageNo: Number,
    val nomOfRows: Number,
    val listSize: Number,
    val historyPings: List<UsingHistory>
)

data class UsingHistory(
    val historyType: Number,
    val reward: String,
    val description: String,
    val useDate: String
)

data class ExpiredPings(
    val memberId: String,
    val pageNo: Number,
    val nomOfRows: Number,
    val listSize: Number,
    val expirationPings: List<ExpiredHistory>
)

data class ExpiredHistory(
    val type: Int,
    val typeStr: String,
    val reward: String,
    val targetName: String,
    val description: String,
    val saveDate: String,
    val useStartDate: String,
    val useEndDate: String
)