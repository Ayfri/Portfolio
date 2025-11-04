package io.github.ayfri.data

import kotlinx.js.JsPlainObject


@JsPlainObject
external interface GitHubData {
	val ayfri: GitHubUser
	val repos: List<GitHubRepository>
}

@JsPlainObject
external interface GitHubUser {
	val bio: String
	val createdAt: String
	val followers: Int
	val following: Int
	val hireable: Boolean
	val htmlUrl: String
	val login: String
	val name: String
	val publicRepos: Int
	val updatedAt: String
}

@JsPlainObject
external interface GitHubRepository {
	val archived: Boolean
	val commitsCount: Int
	val contributorsCount: Int
	val createdAt: String
	val defaultBranch: String
	val description: String?
	val fork: Boolean
	val forksCount: Int
	val fullName: String
	val homepage: String?
	val htmlUrl: String
	val id: Int
	val isTemplate: Boolean
	val language: String?
	val name: String
	val openIssuesCount: Int
	val owner: PartialUser
	val `private`: Boolean
	val pushedAt: String
	val readmeContent: String?
	val size: Int
	val stargazersCount: Int
	val topics: List<String>
	val updatedAt: String
	val visibility: String
	val watchersCount: Int
}

@JsPlainObject
external interface PartialUser {
	val avatarUrl: String
	val eventsUrl: String
	val followersUrl: String
	val followingUrl: String
	val gistsUrl: String
	val gravatarId: String
	val htmlUrl: String
	val id: Int
	val login: String
	val nodeId: String
	val organizationsUrl: String
	val receivedEventsUrl: String
	val reposUrl: String
	val siteAdmin: Boolean
	val starredUrl: String
	val subscriptionsUrl: String
	val type: String
	val url: String
}
