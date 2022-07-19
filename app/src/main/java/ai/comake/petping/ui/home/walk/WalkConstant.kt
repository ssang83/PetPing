package ai.comake.petping.ui.home.walk

/*
 산책 상태
 */
const val STOP_WALK_OVER_TIME_MINUTE = 60L
const val PAUSE_OVER_TIME_MINUTE = 10L
const val WALK_MAX_SPEED_MS = 10F

enum class WalkBottomUi {
    NONE, CONTROL, MARKING, PLACE, CLUSTER, GUIDE
}

enum class WalkState(state: Int) {
    START_STATE(2),
    PAUSE_STATE(3)
}

enum class EndState(code: Int) {
    NORMAL_END(1),
    FORCE_END(2),
    OVER_WALK_SPEED_END(3),
    NO_CHANGE_LOCATION_END(4),
    OVER_TIME_PAUSE_END(5),
    NETWORK_OFF(8),
    MAX_WALK_TIME(10)
}