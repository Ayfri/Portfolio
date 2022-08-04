package entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
	@SerialName("avatar_url")
	val avatarUrl: String,
	val bio: String,
	val blog: String,
	val collaborators: Int,
	val company: String,
	@SerialName("created_at")
	val createdAt: String,
	@SerialName("disk_usage")
	val diskUsage: Int,
	val email: String,
	@SerialName("events_url")
	val eventsUrl: String,
	val followers: Int,
	@SerialName("followers_url")
	val followersUrl: String,
	val following: Int,
	@SerialName("following_url")
	val followingUrl: String,
	@SerialName("gists_url")
	val gistsUrl: String,
	@SerialName("gravatar_id")
	val gravatarId: String,
	val hireable: Boolean,
	@SerialName("html_url")
	val htmlUrl: String,
	val id: Int,
	val location: String,
	val login: String,
	val name: String,
	@SerialName("node_id")
	val nodeId: String,
	@SerialName("organizations_url")
	val organizationsUrl: String,
	@SerialName("owned_private_repos")
	val ownedPrivateRepos: Int,
	val plan: Plan,
	@SerialName("private_gists")
	val privateGists: Int,
	@SerialName("public_gists")
	val publicGists: Int,
	@SerialName("public_repos")
	val publicRepos: Int,
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
	@SerialName("total_private_repos")
	val totalPrivateRepos: Int,
	@SerialName("twitter_username")
	val twitterUsername: String,
	@SerialName("two_factor_authentication")
	val twoFactorAuthentication: Boolean,
	val type: String,
	@SerialName("updated_at")
	val updatedAt: String,
	val url: String,
)

@Serializable
data class Plan(
	val collaborators: Int,
	val name: String,
	@SerialName("private_repos")
	val privateRepos: Int,
	val space: Int,
)
