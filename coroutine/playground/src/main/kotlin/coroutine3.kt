import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun main() {
    println("Before")
    val u = requestInt()
    println(u)
    println("After")
}

suspend fun requestInt(): Int {
    val u = 1
    return suspendCoroutine<Int> { continuation ->
        requestInt { u ->
            continuation.resume(u)
        }
    }
}