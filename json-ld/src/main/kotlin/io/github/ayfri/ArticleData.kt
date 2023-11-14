package io.github.ayfri

data class ArticleData(
	val navTitle: String,
	val title: String,
	val description: String,
	val date: String,
	val root: String,
)

fun readArticleData(markdown: String): ArticleData {
	val startIndex = 0
	var endIndex = -1
	markdown.lines().drop(1).forEachIndexed { index, it ->
		if (it.startsWith("---")) {
			endIndex = index
		}
	}

	require(endIndex != -1) { "No front matter found in $markdown" }

	val frontMatter = markdown.lines().subList(startIndex, endIndex)
	val navTitle = frontMatter.find { it.startsWith("nav-title") }?.split(":")?.get(1)?.trim() ?: ""
	val title = frontMatter.find { it.startsWith("title") }?.split(":")?.get(1)?.trim() ?: ""
	val description = frontMatter.find { it.startsWith("description") }?.split(":")?.get(1)?.trim() ?: ""
	val date = frontMatter.find { it.startsWith("date") }?.split(":")?.get(1)?.trim() ?: ""
	val root = frontMatter.find { it.startsWith("root") }?.split(":")?.get(1)?.trim() ?: ""

	return ArticleData(navTitle, title, description, date, root)
}
