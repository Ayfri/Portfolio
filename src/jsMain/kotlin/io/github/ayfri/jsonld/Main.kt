package io.github.ayfri.jsonld

import com.varabyte.kobweb.core.AppGlobals
import io.github.ayfri.articles.articlesEntries
import io.github.ayfri.ensureSuffix

fun generateJsonLD(path: String) = articlesEntries.find { it.path == path }?.let {
	BlogArticleJsonLD(
		author = PersonJsonLD(
			name = "Ayfri",
			sameAs = listOf(
				"https://github.com/Ayfri"
			),
			url = AppGlobals["url"],
		),
		datePublished = it.date,
		dateModified = it.dateModified,
		headline = it.title,
		keywords = it.navTitle.split(" "),
		url = AppGlobals["url"] + it.path.ensureSuffix("/"),
	)
}
