package entities

import kotlinx.serialization.Serializable

@Serializable
data class User(
	val avatarUrl: String,
	val bio: String,
	val blog: String,
	val collaborators: Int,
	val company: String?,
	val createdAt: String,
	val diskUsage: Int,
	val email: String,
	val eventsUrl: String,
	val followers: Int,
	val followersUrl: String,
	val following: Int,
	val followingUrl: String,
	val gistsUrl: String,
	val gravatarId: String,
	val hireable: Boolean?,
	val htmlUrl: String,
	val id: Int,
	val location: String,
	val login: String,
	val name: String,
	val nodeId: String,
	val organizationsUrl: String,
	val ownedPrivateRepos: Int,
	val plan: Plan,
	val privateGists: Int,
	val publicGists: Int,
	val publicRepos: Int,
	val receivedEventsUrl: String,
	val reposUrl: String,
	val siteAdmin: Boolean,
	val starredUrl: String,
	val subscriptionsUrl: String,
	val totalPrivateRepos: Int,
	val twitterUsername: String,
	val twoFactorAuthentication: Boolean,
	val type: String,
	val updatedAt: String,
	val url: String,
)

@Serializable
data class Plan(
	val collaborators: Int,
	val name: String,
	val privateRepos: Int,
	val space: Int,
)
