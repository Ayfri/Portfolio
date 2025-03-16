package io.github.ayfri.jsonld

import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.articlesEntries
import io.github.ayfri.data.GitHubRepository
import io.github.ayfri.data.LINKEDIN_LINK
import io.github.ayfri.data.TWITCH_LINK
import io.github.ayfri.data.TWITTER_LINK
import io.github.ayfri.ensureSuffix

const val GITHUB_PROFILE = "https://github.com/Ayfri"

val socialMediaLinks = listOf(
	GITHUB_PROFILE,
	LINKEDIN_LINK,
	TWITCH_LINK,
	TWITTER_LINK
)

fun generateJsonLD(path: String) = GraphJsonLD(
	listOfNotNull(
		getArticleJsonLD(path),
		defaultJsonLD(),
	)
)

fun generateProjectJsonLD(project: GitHubRepository, path: String) = GraphJsonLD(
	listOf(
		getProjectJsonLD(project, path),
		getProjectBreadcrumbJsonLD(project, path),
	)
)

fun generateProjectsListJsonLD(projects: List<GitHubRepository>) = GraphJsonLD(
	listOf(
		getProjectsListJsonLD(projects),
	)
)

fun getArticleJsonLD(path: String) = articlesEntries.find { it.path == path }?.let {
	BlogArticleJsonLD(
		author = PersonJsonLD(
			name = "Ayfri",
			sameAs = socialMediaLinks,
			url = AppGlobals["url"],
		),
		datePublished = it.date,
		dateModified = it.dateModified,
		inLanguage = "en-US",
		headline = it.title,
		keywords = it.keywords,
		url = AppGlobals["url"] + it.path.ensureSuffix("/"),
	)
}

fun getProjectJsonLD(project: GitHubRepository, path: String) = SoftwareSourceCodeJsonLD(
	name = project.name,
	description = project.description,
	author = PersonJsonLD(
		name = project.owner.login,
		image = project.owner.avatarUrl,
		url = project.owner.htmlUrl,
	),
	codeRepository = project.htmlUrl,
	programmingLanguage = project.language,
	dateCreated = project.createdAt,
	dateModified = project.updatedAt,
	keywords = project.topics,
	url = AppGlobals["url"]!! + path.ensureSuffix("/"),
)

fun getProjectBreadcrumbJsonLD(project: GitHubRepository, path: String) = BreadcrumbListJsonLD(
	itemListElement = listOf(
		ListItemJsonLD(
			position = 1,
			item = WebPageJsonLD(
				headline = "Home",
				url = AppGlobals["url"]!!,
			)
		),
		ListItemJsonLD(
			position = 2,
			item = WebPageJsonLD(
				headline = "Projects",
				url = AppGlobals["url"]!! + "/projects/",
			)
		),
		ListItemJsonLD(
			position = 3,
			item = WebPageJsonLD(
				headline = project.owner.login,
				url = AppGlobals["url"]!! + "/projects/?user=${project.owner.login}",
			)
		),
		ListItemJsonLD(
			position = 4,
			item = WebPageJsonLD(
				headline = project.name,
				url = AppGlobals["url"]!! + path.ensureSuffix("/"),
			)
		)
	)
)

fun getProjectsListJsonLD(projects: List<GitHubRepository>) = ItemListJsonLD(
	itemListElement = projects.take(10).mapIndexed { index, project ->
		ListItemJsonLD(
			position = index + 1,
			item = SoftwareSourceCodeJsonLD(
				name = project.name,
				description = project.description,
				author = PersonJsonLD(
					name = project.owner.login,
					image = project.owner.avatarUrl,
					url = project.owner.htmlUrl,
				),
				codeRepository = project.htmlUrl,
				programmingLanguage = project.language,
				url = AppGlobals["url"]!! + "/projects/${project.fullName}/",
			)
		)
	}
)

fun defaultJsonLD() = WebSiteJsonLD(
	name = AppGlobals["author"]!!,
	url = AppGlobals["url"]!!,
	sameAs = socialMediaLinks,
)
