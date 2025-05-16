package entities

import GitHubAPI
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Repository(
	val archiveUrl: String,
	val archived: Boolean,
	val assigneesUrl: String,
	val blobsUrl: String,
	val branchesUrl: String,
	val cloneUrl: String,
	val collaboratorsUrl: String,
	val commentsUrl: String,
	val commitsUrl: String,
	val compareUrl: String,
	val contentsUrl: String,
	val contributorsUrl: String,
	val createdAt: String,
	val defaultBranch: String,
	val deploymentsUrl: String,
	val description: String?,
	val disabled: Boolean,
	val downloadsUrl: String,
	val eventsUrl: String,
	val fork: Boolean,
	val forks: Int,
	val forksCount: Int,
	val forksUrl: String,
	val fullName: String,
	val gitCommitsUrl: String,
	val gitRefsUrl: String,
	val gitTagsUrl: String,
	val gitUrl: String,
	val hasDownloads: Boolean,
	val hasIssues: Boolean,
	val hasPages: Boolean,
	val hasProjects: Boolean,
	val hasWiki: Boolean,
	val homepage: String?,
	val hooksUrl: String,
	val htmlUrl: String,
	val id: Int,
	val isTemplate: Boolean,
	val issueCommentUrl: String,
	val issueEventsUrl: String,
	val issuesUrl: String,
	val keysUrl: String,
	val labelsUrl: String,
	val language: String?,
	val languagesUrl: String,
	val license: License?,
	val mergesUrl: String,
	val milestonesUrl: String,
	val mirrorUrl: String?,
	val name: String,
	val nodeId: String,
	val notificationsUrl: String,
	val openIssues: Int,
	val openIssuesCount: Int,
	val owner: PartialUser,
	val permissions: Permissions? = null,
	@SerialName("private")
	val `private`: Boolean,
	val pullsUrl: String,
	val pushedAt: String,
	val releasesUrl: String,
	val size: Int,
	val sshUrl: String,
	val stargazersCount: Int,
	val stargazersUrl: String,
	val statusesUrl: String,
	val subscribersUrl: String,
	val subscriptionUrl: String,
	val svnUrl: String,
	val tagsUrl: String,
	val teamsUrl: String,
	val topics: List<String>,
	val treesUrl: String,
	val updatedAt: String,
	val url: String,
	val visibility: String,
	val watchers: Int,
	val watchersCount: Int,
	val webCommitSignoffRequired: Boolean,
) {
	suspend fun getREADME() = GitHubAPI.ktorClient.get {
		url("${this@Repository.url}/readme?ref=$defaultBranch")
	}.let {
		when (it.status) {
			HttpStatusCode.NotFound -> null

			else -> {
				val encoded = it.body<ReadMe>().content
				String(Base64.getMimeDecoder().decode(encoded))
			}
		}
	}

	suspend fun getCount(url: String, default: Int = 0) = GitHubAPI.ktorClient.get {
		url(url)
	}.headers["Link"]?.substringAfterLast("page=")?.substringBeforeLast(">")?.toIntOrNull() ?: default

	suspend fun getCommitsCount() = getCount("${this@Repository.url}/commits?sha=${defaultBranch}&per_page=1&page=1", 1)

	suspend fun getWatchersCount() = getCount("${this@Repository.url}/subscribers?per_page=1&page=1")

	suspend fun getContributorsCount() = getCount("${this@Repository.url}/contributors?per_page=1&page=1")
}

@Serializable
data class PartialUser(
	val avatarUrl: String,
	val eventsUrl: String,
	val followersUrl: String,
	val followingUrl: String,
	val gistsUrl: String,
	val gravatarId: String,
	val htmlUrl: String,
	val id: Int,
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

@Serializable
data class Permissions(
	val admin: Boolean,
	val maintain: Boolean,
	val pull: Boolean,
	val push: Boolean,
	val triage: Boolean,
)

@Serializable
data class License(
	val key: String,
	val name: String,
	val nodeId: String,
	val spdxId: String,
	val url: String?,
)

@Serializable
data class ReadMe(
	val content: String?,
	val downloadUrl: String,
	val encoding: String,
	val gitUrl: String,
	val htmlUrl: String,
	@SerialName("_links")
	val links: Links,
	val name: String,
	val path: String,
	val sha: String,
	val size: Int,
	val type: String,
	val url: String,
)

@Serializable
data class Links(
	val git: String,
	val html: String,
	val self: String,
)

enum class GetRepositoryDirection {
	ASC,
	DESC
}

enum class GetRepositorySort {
	CREATED,
	UPDATED,
	PUSHED,
	FULL_NAME
}

enum class GetRepositoryType {
	ALL,
	OWNER,
	MEMBER
}
