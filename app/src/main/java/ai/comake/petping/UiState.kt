package ai.comake.petping

sealed class UiState {
    object Loading : UiState()
    object Success : UiState()
    data class Failure(val e: Throwable? = null) : UiState()
}

