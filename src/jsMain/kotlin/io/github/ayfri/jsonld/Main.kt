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
	context = "https://schema.org",
	graph = listOfNotNull(
		getArticleJsonLD(path),
		defaultJsonLD(),
	).toTypedArray(),
	type = "Graph",
)

fun generateProjectJsonLD(project: GitHubRepository, path: String) = GraphJsonLD(
	context = "https://schema.org",
	graph = arrayOf(
		getProjectJsonLD(project, path),
		getProjectBreadcrumbJsonLD(project, path),
	),
	type = "Graph",
)

fun generateProjectsListJsonLD(projects: List<GitHubRepository>) = GraphJsonLD(
	context = "https://schema.org",
	graph = arrayOf(
		getProjectsListJsonLD(projects),
	),
	type = "Graph",
)

fun getArticleJsonLD(path: String) = articlesEntries.find { it.path == path }?.let {
	BlogArticleJsonLD(
		author = PersonJsonLD(
			context = "https://schema.org",
			image = null,
			name = "Ayfri",
			sameAs = socialMediaLinks.toTypedArray(),
			type = "Person",
			url = AppGlobals["url"],
		),
		context = "https://schema.org",
		dateModified = it.dateModified,
		datePublished = it.date,
		headline = it.title,
		image = null,
		inLanguage = "en-US",
		keywords = it.keywords.toTypedArray(),
		mainEntityOfPage = WebPageJsonLD(
			context = "https://schema.org",
			headline = it.title,
			image = null,
			keywords = it.keywords.toTypedArray(),
			type = "WebPage",
			url = AppGlobals["url"] + it.path.ensureSuffix("/"),
		),
		type = "BlogPosting",
		url = AppGlobals["url"] + it.path.ensureSuffix("/"),
	)
}

fun getProjectJsonLD(project: GitHubRepository, path: String) = SoftwareSourceCodeJsonLD(
	author = PersonJsonLD(
		context = "https://schema.org",
		image = project.owner.avatarUrl,
		name = project.owner.login,
		sameAs = null,
		type = "Person",
		url = project.owner.htmlUrl,
	),
	codeRepository = project.htmlUrl,
	context = "https://schema.org",
	dateCreated = project.createdAt,
	dateModified = project.updatedAt,
	description = project.description,
	keywords = project.topics,
	license = null,
	name = project.name,
	programmingLanguage = project.language,
	type = "SoftwareSourceCode",
	url = AppGlobals["url"]!! + path.ensureSuffix("/"),
)

fun getProjectBreadcrumbJsonLD(project: GitHubRepository, path: String) = BreadcrumbListJsonLD(
	context = "https://schema.org",
	itemListElement = arrayOf(
		ListItemJsonLD(
			context = "https://schema.org",
			item = WebPageJsonLD(
				context = "https://schema.org",
				headline = "Home",
				image = null,
				keywords = null,
				type = "WebPage",
				url = AppGlobals["url"]!!,
			),
			position = 1,
			type = "ListItem",
		),
		ListItemJsonLD(
			context = "https://schema.org",
			item = WebPageJsonLD(
				context = "https://schema.org",
				headline = "Projects",
				image = null,
				keywords = null,
				type = "WebPage",
				url = AppGlobals["url"]!! + "/projects/",
			),
			position = 2,
			type = "ListItem",
		),
		ListItemJsonLD(
			context = "https://schema.org",
			item = WebPageJsonLD(
				context = "https://schema.org",
				headline = project.owner.login,
				image = null,
				keywords = null,
				type = "WebPage",
				url = AppGlobals["url"]!! + "/projects/?user=${project.owner.login}",
			),
			position = 3,
			type = "ListItem",
		),
		ListItemJsonLD(
			context = "https://schema.org",
			item = WebPageJsonLD(
				context = "https://schema.org",
				headline = project.name,
				image = null,
				keywords = null,
				type = "WebPage",
				url = AppGlobals["url"]!! + path.ensureSuffix("/"),
			),
			position = 4,
			type = "ListItem",
		)
	),
	type = "BreadcrumbList",
)

fun getProjectsListJsonLD(projects: List<GitHubRepository>) = ItemListJsonLD(
	context = "https://schema.org",
	itemListElement = projects.take(10).mapIndexed { index, project ->
		ListItemJsonLD(
			context = "https://schema.org",
			item = SoftwareSourceCodeJsonLD(
				author = PersonJsonLD(
					context = "https://schema.org",
					image = project.owner.avatarUrl,
					name = project.owner.login,
					sameAs = null,
					type = "Person",
					url = project.owner.htmlUrl,
				),
				codeRepository = project.htmlUrl,
				context = "https://schema.org",
				dateCreated = null,
				dateModified = null,
				description = project.description,
				keywords = null,
				license = null,
				name = project.name,
				programmingLanguage = project.language,
				type = "SoftwareSourceCode",
				url = AppGlobals["url"]!! + "/projects/${project.fullName}/",
			),
			position = index + 1,
			type = "ListItem",
		)
	}.toTypedArray(),
	numberOfItems = projects.take(10).size,
	type = "ItemList",
)

fun defaultJsonLD() = WebSiteJsonLD(
	context = "https://schema.org",
	name = AppGlobals["author"]!!,
	sameAs = socialMediaLinks.toTypedArray(),
	type = "WebSite",
	url = AppGlobals["url"]!!,
)
