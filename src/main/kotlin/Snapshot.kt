import kotlinx.serialization.Serializable

/** The content of `result.json`. Every class declares only the fields the site reads: a field GitHub renames or nulls can't break the run when it isn't declared. */
@Serializable
data class Snapshot(
	val ayfri: User,
	val repos: List<Repository>,
)

@Serializable
data class User(
	val bio: String,
	val createdAt: String,
	val followers: Int,
	val following: Int,
	/** GitHub sends null when the profile doesn't say, read as false through `coerceInputValues`. */
	val hireable: Boolean = false,
	val htmlUrl: String,
	val login: String,
	val name: String,
	val publicRepos: Int,
	val updatedAt: String,
)

/**
 * A repository as GitHub lists it, plus four fields the list doesn't hold, fetched per repository by [GitHubAPI]:
 * [commitsCount], [contributorsCount], [readmeContent] and [watchersCount].
 */
@Serializable
data class Repository(
	val archived: Boolean,
	val commitsCount: Int = 0,
	val contributorsCount: Int = 0,
	val createdAt: String,
	val defaultBranch: String,
	val description: String?,
	val fork: Boolean,
	val forksCount: Int,
	val fullName: String,
	val homepage: String?,
	val htmlUrl: String,
	/** Repository ids pass 1.3 billion in 2026, on their way to the `Int` limit of 2.1 billion. */
	val id: Long,
	val isTemplate: Boolean,
	val language: String?,
	val name: String,
	val openIssuesCount: Int,
	val owner: PartialUser,
	val `private`: Boolean,
	val pushedAt: String,
	val readmeContent: String? = null,
	val size: Int,
	val stargazersCount: Int,
	val topics: List<String>,
	val updatedAt: String,
	val visibility: String,
	/** The people watching the repository. The `watchers_count` GitHub lists is a legacy copy of the star count, replaced by the real one. */
	val watchersCount: Int = 0,
)

@Serializable
data class PartialUser(
	val avatarUrl: String,
	val eventsUrl: String,
	val followersUrl: String,
	val followingUrl: String,
	val gistsUrl: String,
	val gravatarId: String,
	val htmlUrl: String,
	val id: Long,
	val login: String,
	val nodeId: String,
	val organizationsUrl: String,
	val receivedEventsUrl: String,
	val reposUrl: String,
	val siteAdmin: Boolean,
	val starredUrl: String,
	val subscriptionsUrl: String,
	val type: String,
	val url: String,
)
