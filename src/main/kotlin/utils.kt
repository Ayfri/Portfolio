import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

private const val MAX_CONCURRENT_REQUESTS = 10

suspend fun <T, R> List<T>.mapConcurrently(mapper: suspend (T) -> R): List<R> = coroutineScope {
	val semaphore = Semaphore(MAX_CONCURRENT_REQUESTS)
	map { value ->
		async {
			semaphore.withPermit { mapper(value) }
		}
	}.awaitAll()
}
