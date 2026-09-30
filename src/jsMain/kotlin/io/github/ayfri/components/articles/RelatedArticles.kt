package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import io.github.ayfri.articlesEntries
import io.github.ayfri.data.ArticleEntry
import io.github.ayfri.pages.IconHeading
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

/** Up to 3 other articles sharing the most keywords, or the most recent ones when none overlap. */
fun findRelatedArticles(currentPath: String, keywords: List<String>): List<ArticleEntry> {
	val others = articlesEntries.filter { it.path != currentPath }
	val matched = others
		.map { article -> article to article.keywords.count { it in keywords } }
		.filter { it.second > 0 }
		.sortedByDescending { it.second }
		.map { it.first }

	return matched.ifEmpty { others.sortedByDescending { it.date } }.take(3)
}

@Composable
fun RelatedArticles(articles: List<ArticleEntry>) {
	Style(ArticleListStyle)

	Section({
		style { marginTop(4.cssRem) }
	}) {
		IconHeading("book-bookmark", "Keep reading")
		ArticleList(articles)
	}
}
