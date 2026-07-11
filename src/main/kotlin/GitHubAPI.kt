import entities.GetRepositoryDirection
import entities.GetRepositorySort
import entities.GetRepositoryType
import entities.Repository
import entities.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

data object GitHubAPI {
	private const val BASE_URL = "https://api.github.com"
	private const val USER = "Ayfri"
	private const val PER_PAGE = 100
	private const val REQUEST_TIMEOUT_MILLIS = 30_000L

	private val token = System.getenv("GITHUB_TOKEN") ?: error("GITHUB_TOKEN is not set")

	@OptIn(ExperimentalSerializationApi::class)
	val client = HttpClient(CIO) {
		defaultRequest {
			header(HttpHeaders.Accept, "application/vnd.github+json")
			header(HttpHeaders.UserAgent, "Ayfri-Portfolio-Data")
			bearerAuth(token)
		}

		install(HttpTimeout) {
			requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
		}

		install(ContentNegotiation) {
			json(Json {
				coerceInputValues = true
				ignoreUnknownKeys = true
				namingStrategy = JsonNamingStrategy.SnakeCase
			})
		}

	}

	suspend fun getUser(): User = client.get("$BASE_URL/users/$USER").body()

	suspend fun getUserRepos(
		type: GetRepositoryType = GetRepositoryType.ALL,
		sort: GetRepositorySort = GetRepositorySort.FULL_NAME,
		direction: GetRepositoryDirection = when (sort) {
			GetRepositorySort.FULL_NAME -> GetRepositoryDirection.ASC
			else -> GetRepositoryDirection.DESC
		},
		perPage: Int = 30,
		page: Int = 1,
	): List<Repository> = client.get {
		url("$BASE_URL/users/$USER/repos")

		parameter("type", type.name.lowercase())
		parameter("sort", sort.name.lowercase())
		parameter("direction", direction.name.lowercase())
		parameter("per_page", perPage)
		parameter("page", page)
	}.body<List<Repository>>()

	suspend fun getAllUserRepos(
		type: GetRepositoryType = GetRepositoryType.ALL,
		sort: GetRepositorySort = GetRepositorySort.FULL_NAME,
		direction: GetRepositoryDirection = when (sort) {
			GetRepositorySort.FULL_NAME -> GetRepositoryDirection.ASC
			else -> GetRepositoryDirection.DESC
		},
	): List<Repository> {
		val list = mutableListOf<Repository>()
		val response = client.get {
			url("$BASE_URL/users/$USER/repos")

			parameter("type", type.name.lowercase())
			parameter("sort", sort.name.lowercase())
			parameter("direction", direction.name.lowercase())
			parameter("per_page", PER_PAGE)
		}

		list.addAll(response.body())

		for (page in 2..response.headers.lastPage()) {
			list += getUserRepos(type, sort, direction, PER_PAGE, page)
		}

		return list
	}
}

fun Headers.lastPage(default: Int = 1): Int = this[HttpHeaders.Link]
	?.split(',')
	?.firstOrNull { "rel=\"last\"" in it }
	?.substringAfter("page=")
	?.substringBefore('&')
	?.toIntOrNull()
	?: default
