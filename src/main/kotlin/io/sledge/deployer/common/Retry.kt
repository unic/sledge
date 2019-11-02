package io.sledge.deployer.common

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

suspend fun <T> retry(retries: Int = 5, delay: Long = 1000, block: (Long) -> T): T {
    for (i in 1..retries) {
        try {
            return withTimeout(5000) {
                block(i.toLong())
            }
        } catch (timeoutException: TimeoutCancellationException) {
        } catch (e: Exception) {
        }
        delay(delay)
    }
    return block(0)
}
