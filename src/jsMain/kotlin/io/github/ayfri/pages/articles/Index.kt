package io.github.ayfri.pages.articles

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFunnel
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSearch
import com.varabyte.kobweb.silk.components.icons.lucide.LucideTags
import io.github.ayfri.AppStyle
import io.github.ayfri.articlesEntries
import io.github.ayfri.components.ChipGroup
import io.github.ayfri.components.FiltersStyle
import io.github.ayfri.components.SearchField
import io.github.ayfri.components.articles.ArticleList
import io.github.ayfri.components.articles.ArticleListStyle
import io.github.ayfri.components.countedByFrequency
import io.github.ayfri.data.ArticleEntry
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.pages.IconStyle
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

enum class SortOption(val displayName: String, val sort: (List<ArticleEntry>) -> List<ArticleEntry>) {
	NEWEST_FIRST("Newest first", { list -> list.sortedByDescending { it.date } }),
	OLDEST_FIRST("Oldest first", { list -> list.sortedBy { it.date } }),
	ALPHABETICAL("A-Z", { list -> list.sortedBy { it.title } }),
}

/** Tags used by a single article only add noise to the filter row. */
private val articleTags = articlesEntries.flatMap { it.keywords }.countedByFrequency().filter { it.second > 1 }

@Page
@Composable
fun ArticleList() {
	PageLayout(
		"Blog Posts",
		description = "Tutorials and deep dives by Pierre Roy (Ayfri) on Kotlin, Minecraft datapacks with Kore, Svelte, and AI side projects.",
		keywords = "Kotlin blog, Minecraft datapack tutorial, Kore library, Svelte 5 tutorial, programming blog, kotlinx.serialization, web development articles",
	) {
		Style(ArticleListStyle)
		Style(IconStyle)
		Style(BlogPageStyle)

		var searchQuery by remember { mutableStateOf("") }
		var selectedTag by remember { mutableStateOf("") }
		var sortOption by remember { mutableStateOf(SortOption.NEWEST_FIRST) }

		val articles = remember(searchQuery, selectedTag, sortOption) {
			sortOption.sort(articlesEntries.filter { article ->
				(selectedTag.isEmpty() || selectedTag in article.keywords) && (
					searchQuery.isEmpty() ||
						article.title.contains(searchQuery, true) ||
						article.desc.contains(searchQuery, true) ||
						article.keywords.any { it.contains(searchQuery, true) }
					)
			})
		}

		Div({
			classes(AppStyle.sections, BlogPageStyle.container)
		}) {
			H1({
				classes(AppStyle.title, BlogPageStyle.title)
			}) {
				Text("Articles")
			}

			P({
				classes(BlogPageStyle.intro)
			}) {
				Text("Tutorials and write-ups on Kotlin, Minecraft datapacks with Kore, Svelte and the side projects I build along the way.")
			}

			Div({
				classes(BlogPageStyle.controls)
			}) {
				SearchField(searchQuery, "Search articles") { searchQuery = it }

				Select({
					classes(AppStyle.input)
					attr("aria-label", "Sort articles")
					onChange { event -> sortOption = SortOption.entries.first { it.name == event.value } }
				}) {
					SortOption.entries.forEach { option ->
						Option(option.name, { if (option == sortOption) selected() }) {
							Text(option.displayName)
						}
					}
				}
			}

			ChipGroup("Filter by tag", { LucideTags() }, articleTags, selectedTag) { selectedTag = it }

			P({
				classes(FiltersStyle.label, BlogPageStyle.resultsInfo)
			}) {
				Span({ classes(IconStyle.label) }) {
					if (articles.isEmpty()) LucideSearch() else LucideFunnel()
					Text(if (articles.isEmpty()) "No article matches these filters." else "Showing ${articles.size} article${if (articles.size != 1) "s" else ""}")
				}
			}

			ArticleList(entries = articles)
		}
	}
}

object BlogPageStyle : StyleSheet() {
	val container by style {
		maxWidth(60.cssRem)
	}

	val title by style {
		marginBottom(.8.cssRem)
	}

	val intro by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		fontSize(1.15.cssRem)
		margin(0.px, 0.px, 2.cssRem)
		maxWidth(40.cssRem)
	}

	val controls by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.75.cssRem)
		marginBottom(1.25.cssRem)
	}

	val resultsInfo by style {
		margin(1.5.cssRem, 0.px, 1.cssRem)
	}
}
