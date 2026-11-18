package io.github.ayfri.jsonld

import io.github.ayfri.data.*
import js.undefined.undefinedOrNull

private const val PERSON_ID = "https://ayfri.com/#person"
private const val WEBSITE_ID = "https://ayfri.com/#website"
private const val SITE_URL = "https://ayfri.com/"
const val AVATAR_URL = "https://ayfri.com/images/avatar.png"

private val socialMediaLinks = arrayOf(GITHUB_PROFILE, LINKEDIN_LINK, TWITCH_LINK, TWITTER_LINK)

private fun person() = PersonJsonLD(
	alternateName = "Ayfri",
	id = PERSON_ID,
	image = AVATAR_URL,
	jobTitle = "Full-Stack and AI Developer",
	name = "Pierre Roy",
	sameAs = socialMediaLinks,
	type = "Person",
	url = "${SITE_URL}about-me/",
)

private fun website() = WebSiteJsonLD(
	alternateName = "Ayfri",
	id = WEBSITE_ID,
	inLanguage = "en",
	name = "Pierre Roy",
	publisher = NodeRef(PERSON_ID),
	type = "WebSite",
	url = SITE_URL,
)

private fun breadcrumb(vararg crumbs: Pair<String, String>) = BreadcrumbListJsonLD(
	itemListElement = crumbs.mapIndexed { index, (name, url) ->
		ListItemJsonLD(item = url, name = name, position = index + 1, type = "ListItem")
	}.toTypedArray(),
	type = "BreadcrumbList",
)

enum class PageType { WebPage, ProfilePage, CollectionPage }

fun pageGraph(canonical: String, type: PageType, name: String, description: String, vararg nodes: JsonLD) = GraphJsonLD(
	context = "https://schema.org",
	graph = arrayOf(
		person(),
		website(),
		WebPageJsonLD(
			description = description,
			id = "$canonical#webpage",
			inLanguage = "en",
			isPartOf = NodeRef(WEBSITE_ID),
			/** `undefined` is dropped by `JSON.stringify`, `null` is not. */
			mainEntity = if (type == PageType.ProfilePage) NodeRef(PERSON_ID) else undefinedOrNull,
			name = name,
			type = type.name,
			url = canonical,
		),
		*nodes,
	),
)

fun articleNodes(entry: ArticleEntry, canonical: String, description: String, image: String): Array<JsonLD> = arrayOf(
	BlogPostingJsonLD(
		author = NodeRef(PERSON_ID),
		dateModified = entry.dateModified,
		datePublished = entry.date,
		description = description,
		headline = entry.title,
		id = "$canonical#article",
		image = image,
		inLanguage = "en",
		keywords = entry.keywords.toTypedArray(),
		mainEntityOfPage = NodeRef("$canonical#webpage"),
		publisher = NodeRef(PERSON_ID),
		type = "BlogPosting",
		url = canonical,
	),
	breadcrumb("Home" to SITE_URL, "Blog" to "${SITE_URL}articles/", entry.title to canonical),
)

fun projectNodes(project: GitHubRepository, canonical: String): Array<JsonLD> = arrayOf(
	SoftwareSourceCodeJsonLD(
		author = PersonJsonLD(
			image = project.owner.avatarUrl,
			name = project.owner.login,
			type = "Person",
			url = project.owner.htmlUrl,
		),
		codeRepository = project.htmlUrl,
		dateCreated = project.createdAt,
		dateModified = project.updatedAt,
		description = project.description ?: undefinedOrNull,
		keywords = project.topics,
		name = project.name,
		programmingLanguage = project.language ?: undefinedOrNull,
		type = "SoftwareSourceCode",
		url = canonical,
	),
	breadcrumb(
		"Home" to SITE_URL,
		"Projects" to "${SITE_URL}projects/",
		project.owner.login to "${SITE_URL}projects/?user=${project.owner.login}",
		project.name to canonical,
	),
)

/** The ten most-starred projects, pointing at their pages, which the export prerenders from the same snapshot. */
fun projectsListNode(projects: List<GitHubRepository>): ItemListJsonLD {
	val top = projects.take(10)
	return ItemListJsonLD(
		itemListElement = top.mapIndexed { index, project ->
			ListItemJsonLD(item = "${SITE_URL}projects/${project.fullName}/", name = project.name, position = index + 1, type = "ListItem")
		}.toTypedArray(),
		numberOfItems = top.size,
		type = "ItemList",
	)
}
