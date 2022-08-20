@file:OptIn(ExperimentalJsExport::class)

package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@JsExport
@Serializable
data class GitHubData(
	val ayfri: GitHubUser,
	val repos: Array<GitHubRepository>,
)

@JsExport
@Serializable
data class GitHubUser(
	val bio: String,
	val createdAt: String,
	val followers: Int,
	val following: Int,
	val hireable: Boolean,
	val htmlUrl: String,
	val login: String,
	val name: String,
	val publicRepos: Int,
	val totalPrivateRepos: Int,
	val updatedAt: String,
)

@JsExport
@Serializable
data class GitHubRepository(
	val archived: Boolean,
	val commitsCount: Int,
	val contributorsCount: Int,
	val createdAt: String,
	val defaultBranch: String,
	val description: String?,
	val fork: Boolean,
	val forksCount: Int,
	val fullName: String,
	val homepage: String?,
	val htmlUrl: String,
	val id: Int,
	val isTemplate: Boolean,
	val language: String?,
	val name: String,
	val openIssuesCount: Int,
	val owner: PartialUser,
	val `private`: Boolean,
	val pushedAt: String,
	val readmeContent: String?,
	val size: Int,
	val stargazersCount: Int,
	val topics: Array<String>,
	val updatedAt: String,
	val visibility: String,
	val watchersCount: Int,
)

@JsExport
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