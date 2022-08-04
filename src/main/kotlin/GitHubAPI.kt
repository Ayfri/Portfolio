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
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object GitHubAPI {
	const val BASE_URL = "https://api.github.com"
	const val RAW_URL = "https://raw.githubusercontent.com"
	const val USER = "Ayfri"
	
	private val TOKEN = System.getenv("GITHUB_TOKEN") ?: throw IllegalStateException("GITHUB_TOKEN is not set")
	
	@OptIn(ExperimentalSerializationApi::class)
	val ktorClient = HttpClient(CIO) {
		defaultRequest {
			basicAuth(USER, TOKEN)
		}
		
		install(ContentNegotiation) {
			json(Json {
				useAlternativeNames = false
				ignoreUnknownKeys = true
				explicitNulls = true
			})
		}
	}
	
	suspend fun getUser() = ktorClient.get("$BASE_URL/users/$USER").body<User>()
	
	suspend fun getUserRepos(
		type: GetRepositoryType = GetRepositoryType.ALL,
		sort: GetRepositorySort = GetRepositorySort.FULL_NAME,
		direction: GetRepositoryDirection = when (sort) {
			GetRepositorySort.FULL_NAME -> GetRepositoryDirection.ASC
			else -> GetRepositoryDirection.DESC
		},
		perPage: Int = 30,
		page: Int = 1,
	) = ktorClient.get {
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
		val response = ktorClient.get {
			url("$BASE_URL/users/$USER/repos")
			parameter("type", type.name.lowercase())
			parameter("sort", sort.name.lowercase())
			parameter("direction", direction.name.lowercase())
			parameter("per_page", 100)
		}
		
		list.addAll(response.body())
		
		response.headers["Links"]?.let {
			val pages = it.substringAfterLast("page=").substringBeforeLast(">").toIntOrNull() ?: 0
			
			for (i in 2..pages) {
				val repos = getUserRepos(type, sort, direction, 100, i)
				list.addAll(repos)
			}
		}
		
		return list
	}
}