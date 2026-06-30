package ir.kenar.core.network

sealed class ApiException(message: String) : Exception(message) {

    class Network(cause: Throwable) : ApiException(cause.message ?: "network error")

   
    class Server(val status: Int, val serverMessage: String?) :
        ApiException(serverMessage ?: "server error ($status)")


    object Unauthenticated : ApiException("not authenticated")
}