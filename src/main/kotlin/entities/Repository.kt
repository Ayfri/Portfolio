package entities

import GitHubAPI
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Repository(
	@SerialName("archive_url")
	val archiveUrl: String,
	val archived: Boolean,
	@SerialName("assignees_url")
	val assigneesUrl: String,
	@SerialName("blobs_url")
	val blobsUrl: String,
	@SerialName("branches_url")
	val branchesUrl: String,
	@SerialName("clone_url")
	val cloneUrl: String,
	@SerialName("collaborators_url")
	val collaboratorsUrl: String,
	@SerialName("comments_url")
	val commentsUrl: String,
	@SerialName("commits_url")
	val commitsUrl: String,
	@SerialName("compare_url")
	val compareUrl: String,
	@SerialName("contents_url")
	val contentsUrl: String,
	@SerialName("contributors_url")
	val contributorsUrl: String,
	@SerialName("created_at")
	val createdAt: String,
	@SerialName("default_branch")
	val defaultBranch: String,
	@SerialName("deployments_url")
	val deploymentsUrl: String,
	val description: String?,
	val disabled: Boolean,
	@SerialName("downloads_url")
	val downloadsUrl: String,
	@SerialName("events_url")
	val eventsUrl: String,
	val fork: Boolean,
	val forks: Int,
	@SerialName("forks_count")
	val forksCount: Int,
	@SerialName("forks_url")
	val forksUrl: String,
	@SerialName("full_name")
	val fullName: String,
	@SerialName("git_commits_url")
	val gitCommitsUrl: String,
	@SerialName("git_refs_url")
	val gitRefsUrl: String,
	@SerialName("git_tags_url")
	val gitTagsUrl: String,
	@SerialName("git_url")
	val gitUrl: String,
	@SerialName("has_downloads")
	val hasDownloads: Boolean,
	@SerialName("has_issues")
	val hasIssues: Boolean,
	@SerialName("has_pages")
	val hasPages: Boolean,
	@SerialName("has_projects")
	val hasProjects: Boolean,
	@SerialName("has_wiki")
	val hasWiki: Boolean,
	val homepage: String?,
	@SerialName("hooks_url")
	val hooksUrl: String,
	@SerialName("html_url")
	val htmlUrl: String,
	val id: Int,
	@SerialName("is_template")
	val isTemplate: Boolean,
	@SerialName("issue_comment_url")
	val issueCommentUrl: String,
	@SerialName("issue_events_url")
	val issueEventsUrl: String,
	@SerialName("issues_url")
	val issuesUrl: String,
	@SerialName("keys_url")
	val keysUrl: String,
	@SerialName("labels_url")
	val labelsUrl: String,
	val language: String?,
	@SerialName("languages_url")
	val languagesUrl: String,
	val license: License?,
	@SerialName("merges_url")
	val mergesUrl: String,
	@SerialName("milestones_url")
	val milestonesUrl: String,
	@SerialName("mirror_url")
	val mirrorUrl: String?,
	val name: String,
	@SerialName("node_id")
	val nodeId: String,
	@SerialName("notifications_url")
	val notificationsUrl: String,
	@SerialName("open_issues")
	val openIssues: Int,
	@SerialName("open_issues_count")
	val openIssuesCount: Int,
	val owner: PartialUser,
	val permissions: Permissions,
	@SerialName("private")
	val `private`: Boolean,
	@SerialName("pulls_url")
	val pullsUrl: String,
	@SerialName("pushed_at")
	val pushedAt: String,
	@SerialName("releases_url")
	val releasesUrl: String,
	val size: Int,
	@SerialName("ssh_url")
	val sshUrl: String,
	@SerialName("stargazers_count")
	val stargazersCount: Int,
	@SerialName("stargazers_url")
	val stargazersUrl: String,
	@SerialName("statuses_url")
	val statusesUrl: String,
	@SerialName("subscribers_url")
	val subscribersUrl: String,
	@SerialName("subscription_url")
	val subscriptionUrl: String,
	@SerialName("svn_url")
	val svnUrl: String,
	@SerialName("tags_url")
	val tagsUrl: String,
	@SerialName("teams_url")
	val teamsUrl: String,
	val topics: List<String>,
	@SerialName("trees_url")
	val treesUrl: String,
	@SerialName("updated_at")
	val updatedAt: String,
	val url: String,
	val visibility: String,
	val watchers: Int,
	@SerialName("watchers_count")
	val watchersCount: Int,
	@SerialName("web_commit_signoff_required")
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
	
	suspend fun getCommitsCount() = GitHubAPI.ktorClient.get {
		url("${this@Repository.url}/commits?sha=${defaultBranch}&per_page=1&page=1")
	}.headers["Link"]?.substringAfterLast("page=")?.substringBeforeLast(">")?.toIntOrNull() ?: 1
	
	suspend fun getWatchersCount() = GitHubAPI.ktorClient.get {
		url("${this@Repository.url}/subscribers?per_page=1&page=1")
	}.headers["Link"]?.substringAfterLast("page=")?.substringBeforeLast(">")?.toIntOrNull() ?: 0
	
	suspend fun getContributorsCount() = GitHubAPI.ktorClient.get {
		url("${this@Repository.url}/contributors?per_page=1&page=1")
	}.headers["Link"]?.substringAfterLast("page=")?.substringBeforeLast(">")?.toIntOrNull() ?: 0
}

@Serializable
data class PartialUser(
	@SerialName("avatar_url")
	val avatarUrl: String,
	@SerialName("events_url")
	val eventsUrl: String,
	@SerialName("followers_url")
	val followersUrl: String,
	@SerialName("following_url")
	val followingUrl: String,
	@SerialName("gists_url")
	val gistsUrl: String,
	@SerialName("gravatar_id")
	val gravatarId: String,
	@SerialName("html_url")
	val htmlUrl: String,
	val id: Int,
	val login: String,
	@SerialName("node_id")
	val nodeId: String,
	@SerialName("organizations_url")
	val organizationsUrl: String,
	@SerialName("received_events_url")
	val receivedEventsUrl: String,
	@SerialName("repos_url")
	val reposUrl: String,
	@SerialName("site_admin")
	val siteAdmin: Boolean,
	@SerialName("starred_url")
	val starredUrl: String,
	@SerialName("subscriptions_url")
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
	@SerialName("node_id")
	val nodeId: String,
	@SerialName("spdx_id")
	val spdxId: String,
	val url: String?,
)

@Serializable
data class ReadMe(
	val content: String?,
	@SerialName("download_url")
	val downloadUrl: String,
	val encoding: String,
	@SerialName("git_url")
	val gitUrl: String,
	@SerialName("html_url")
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