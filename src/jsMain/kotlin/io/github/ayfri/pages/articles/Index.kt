package io.github.ayfri.pages.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import io.github.ayfri.articlesEntries
import io.github.ayfri.components.ArticleList
import io.github.ayfri.components.ArticleListStyle
import io.github.ayfri.layouts.PageLayout
import org.jetbrains.compose.web.css.Style

@Page
@Composable
fun ArticleList() {
	PageLayout("Blog Posts") {
		Style(ArticleListStyle)
		ArticleList(entries = articlesEntries)
	}
}
