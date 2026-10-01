package io.github.ayfri.data

import kotlinx.js.JsPlainObject


@JsPlainObject
external interface GitHubData {
	val repos: Array<GitHubRepository>
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
	val isTemplate: Boolean
	val language: String?
	val name: String
	val openIssuesCount: Int
	val owner: PartialUser
	val pushedAt: String
	val readmeContent: String?
	val size: Int
	val stargazersCount: Int
	val topics: Array<String>
	val updatedAt: String
	val visibility: String
	val watchersCount: Int
}

@JsPlainObject
external interface PartialUser {
	val avatarUrl: String
	val htmlUrl: String
	val login: String
}
