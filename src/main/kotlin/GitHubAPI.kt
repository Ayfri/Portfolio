import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNamingStrategy
import java.util.Base64

@Serializable
private data class ReadMe(val content: String?)

@Serializable
private data class RepositoryDetails(val subscribersCount: Int)

data object GitHubAPI {
	private const val BASE_URL = "https://api.github.com"

	/** Repositories completed at the same time, each one sending its 4 requests one after the other. */
	private const val MAX_CONCURRENT_REPOSITORIES = 10
	private const val PER_PAGE = 100
	private const val REQUEST_TIMEOUT_MILLIS = 30_000L
	private const val USER = "Ayfri"

	/** `[?&]` keeps `per_page=` out of the match, the parameter right before `page=` in every link. */
	private val lastPageRegex = Regex("""[?&]page=(\d+)[^>]*>; rel="last"""")

	private val token = System.getenv("GITHUB_TOKEN") ?: error("GITHUB_TOKEN is not set")

	/** Reads the GitHub responses and writes the snapshot, both in snake case. `encodeDefaults` writes the fields left at 0, false or null. */
	val json = Json {
		coerceInputValues = true
		encodeDefaults = true
		ignoreUnknownKeys = true
		namingStrategy = JsonNamingStrategy.SnakeCase
	}

	private val client = HttpClient(CIO) {
		/** A refused request (rate limit, bad token) stops the run instead of writing a default value, so the previous snapshot stays. */
		expectSuccess = true

		defaultRequest {
			header(HttpHeaders.Accept, "application/vnd.github+json")
			header(HttpHeaders.UserAgent, "Ayfri-Portfolio-Data")
			bearerAuth(token)
		}

		/** Installed before `HttpTimeout`, so the timeout applies to each attempt: the other way round, one timeout covers all the retries. */
		install(HttpRequestRetry) {
			retryOnServerErrors(maxRetries = 3)
			retryOnException(maxRetries = 3, retryOnTimeout = true)
			exponentialDelay()
		}

		install(HttpTimeout) {
			requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
		}

		install(ContentNegotiation) {
			json(json)
		}
	}

	suspend fun getUser(): User = client.get("$BASE_URL/users/$USER").body()

	/** Every public repository the user owns or is a member of, each one with its counts and README. */
	suspend fun getRepositories(): List<Repository> = coroutineScope {
		val firstPage = getRepositoriesPage(1)
		val repositories = firstPage.body<List<Repository>>() +
			(2..(firstPage.headers.lastPage() ?: 1)).flatMap { getRepositoriesPage(it).body<List<Repository>>() }

		val semaphore = Semaphore(MAX_CONCURRENT_REPOSITORIES)
		repositories.map { async { semaphore.withPermit { it.withDetails() } } }.awaitAll()
	}

	private suspend fun getRepositoriesPage(page: Int) = client.get("$BASE_URL/users/$USER/repos") {
		parameter("type", "all")
		parameter("per_page", PER_PAGE)
		parameter("page", page)
	}

	private suspend fun Repository.withDetails(): Repository {
		val url = "$BASE_URL/repos/$fullName"
		return copy(
			commitsCount = getCount("$url/commits"),
			contributorsCount = getCount("$url/contributors"),
			readmeContent = getReadme(url),
			/**
			 * Read from the single repository endpoint, the only one sending `subscribers_count`. Listing `/subscribers` needs access
			 * to the repository, so it answers 404 to the token of the workflow for every repository but its own.
			 */
			watchersCount = client.get(url).body<RepositoryDetails>().subscribersCount,
		)
	}

	/** The README of the default branch, null for a repository without one, which answers 404. */
	private suspend fun getReadme(repositoryUrl: String): String? = try {
		client.get("$repositoryUrl/readme").body<ReadMe>().content?.let { String(Base64.getMimeDecoder().decode(it)) }
	} catch (e: ClientRequestException) {
		if (e.response.status != HttpStatusCode.NotFound) throw e
		null
	}

	/**
	 * The total of a list endpoint readable without access to the repository, asked one item per page so the last page number is the total.
	 * A list of 0 or 1 item has no `Link` header, its total is the size of the only page.
	 * An empty repository answers 409 for its commits and 204 without body for its contributors, both counted as 0.
	 * A history too large to list its contributors (a fork of Linux) answers 403 with a "too large" message, counted as 0 too:
	 * the message tells it apart from a rate limit, also a 403, which has to stop the run.
	 */
	private suspend fun getCount(url: String): Int = try {
		val response = client.get(url) { parameter("per_page", 1) }
		response.headers.lastPage() ?: if (response.status == HttpStatusCode.OK) response.body<JsonArray>().size else 0
	} catch (e: ClientRequestException) {
		val tooLarge = e.response.status == HttpStatusCode.Forbidden && "too large" in e.response.bodyAsText()
		if (e.response.status != HttpStatusCode.Conflict && !tooLarge) throw e
		0
	}

	/** The page number of the `rel="last"` link, null when the response fits in one page: GitHub then sends no `Link` header. */
	private fun Headers.lastPage(): Int? = this[HttpHeaders.Link]?.let { lastPageRegex.find(it) }?.groupValues?.get(1)?.toInt()
}
