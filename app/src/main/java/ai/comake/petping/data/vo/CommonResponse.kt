package ai.comake.petping.data.vo

data class ErrorResponse(
    val code: String,
    val title: String,
    val message: String
)

data class ClientErrorResponse(
    val result: Boolean,
    val status: Number,
    val code: String,
    val message: String
)

data class ServerErrorResponse(
    val error: Error,
    val status: Int = 0
) {
    data class Error(
        val code: String = "",
        val title: String = "",
        val message: String = ""
    )
}

data class CommonResponse<T>(
    val status: String,
    val data: T,
    var error: ErrorResponse
)

data class CommonListResponse<T>(
    val result: Boolean,
    val status: Number,
    val data: List<T>
)
