package io.github.ayfri.data

data class ArticleEntry(
	val path: String,
	val date: String,
	val title: String,
	val desc: String,
	val navTitle: String,
	val keywords: List<String>,
	val dateModified: String,
	/** Estimated reading time in minutes, computed at build time so the article text stays out of the bundle. */
	val readingTime: Int,
	/** Root-relative path of the first Markdown image, `null` when the article has none. */
	val image: String?,
	/** Level and text of every heading of the article body, code blocks excluded, title excluded. */
	val headings: List<Pair<Int, String>>,
)
