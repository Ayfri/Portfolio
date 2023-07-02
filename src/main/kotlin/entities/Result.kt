package entities

import kotlinx.serialization.Serializable

@Serializable
data class Result(
	val ayfri: ResultUser,
	val repos: List<ResultRepository>,
)

@Serializable
data class ResultUser(
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
) {
	companion object {
		fun fromUser(user: User) = ResultUser(
			bio = user.bio,
			createdAt = user.createdAt,
			followers = user.followers,
			following = user.following,
			hireable = user.hireable ?: false,
			htmlUrl = user.htmlUrl,
			login = user.login,
			name = user.name,
			publicRepos = user.publicRepos,
			totalPrivateRepos = user.totalPrivateRepos,
			updatedAt = user.updatedAt,
		)
	}
}

@Serializable
data class ResultRepository(
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
	val topics: List<String>,
	val updatedAt: String,
	val visibility: String,
	val watchersCount: Int,
) {
	companion object {
		suspend fun fromRepository(repository: Repository) = ResultRepository(
			archived = repository.archived,
			commitsCount = repository.getCommitsCount(),
			contributorsCount = repository.getContributorsCount(),
			createdAt = repository.createdAt,
			defaultBranch = repository.defaultBranch,
			description = repository.description,
			fork = repository.fork,
			forksCount = repository.forksCount,
			fullName = repository.fullName,
			homepage = repository.homepage,
			htmlUrl = repository.htmlUrl,
			id = repository.id,
			isTemplate = repository.isTemplate,
			language = repository.language,
			name = repository.name,
			openIssuesCount = repository.openIssuesCount,
			owner = repository.owner,
			`private` = repository.`private`,
			pushedAt = repository.pushedAt,
			readmeContent = repository.getREADME(),
			size = repository.size,
			stargazersCount = repository.watchers,
			topics = repository.topics,
			updatedAt = repository.updatedAt,
			visibility = repository.visibility,
			watchersCount = repository.getWatchersCount(),
		)
	}
}
