package ai.comake.petping.data.vo

class NetworkErrorEvent<T>(val apiCall: suspend () -> T) {
}