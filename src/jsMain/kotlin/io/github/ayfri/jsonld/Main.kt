package io.github.ayfri.jsonld

import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.*

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

fun defaultJsonLD() = WebSiteJsonLD(
	name = AppGlobals["author"]!!,
	url = AppGlobals["url"]!!,
	sameAs = socialMediaLinks,
)
