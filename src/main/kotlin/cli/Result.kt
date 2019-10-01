package cli

sealed class Result {
  data class Ok<T>(val result: T) : Result()
  data class Error(val response: String?) : Result()
}
