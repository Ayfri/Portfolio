package io.github.ayfri.jsonld

import io.github.ayfri.articles.articlesEntries

fun generateJsonLD(path: String) = articlesEntries.find { it.path == path }?.let {
	BlogArticleJsonLD(
		author = PersonJsonLD(
			name = "Ayfri",
			sameAs = listOf(
				"https://github.com/Ayfri"
			),
			url = "https://ayfri.com",
		),
		datePublished = it.date,
		dateModified = it.dateModified,
		headline = it.title,
		keywords = it.navTitle.split(" "),
		url = "https://ayfri.com${it.path}",
	)
}
