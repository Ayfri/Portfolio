package io.github.ayfri.pages.articles

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AppStyle
import io.github.ayfri.articlesEntries
import io.github.ayfri.components.articles.ArticleList
import io.github.ayfri.components.articles.ArticleListStyle
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.utils.focusWithin
import io.github.ayfri.utils.linearGradient
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

enum class SortOption(val displayName: String) {
	NEWEST_FIRST("Newest First"),
	OLDEST_FIRST("Oldest First"),
	ALPHABETICAL("A-Z")
}

@Page
@Composable
fun ArticleList() {
	PageLayout("Blog Posts") {
		Style(ArticleListStyle)
		Style(BlogPageStyle)

		Div({
			classes(BlogPageStyle.container)
		}) {
			Div({
				classes(BlogPageStyle.header)
			}) {
				H1({
					classes(BlogPageStyle.mainTitle)
				}) {
					Text("Blog")
				}

				P({
					classes(BlogPageStyle.intro)
				}) {
					Text("Explore insightful articles on programming, technology, and more. Discover topics that interest you below or use the search function for specific inquiries.")
				}
			}

			var searchQuery by remember { mutableStateOf("") }
			var selectedTag by remember { mutableStateOf<String?>(null) }
			var sortOption by remember { mutableStateOf(SortOption.NEWEST_FIRST) }

			// Extract all unique tags from articles, remove tags only used once, and sort by frequency
			val allTags = remember {
				articlesEntries.flatMap { it.keywords }
					.groupBy { it }
					.filter { (_, count) -> count.size > 1 }
					.map { (tag, usages) -> tag to usages.size }
					.sortedByDescending { it.second }
			}

			Div({
				classes(BlogPageStyle.filterSection)
			}) {
				Div({
					classes(BlogPageStyle.searchBox)
				}) {
					Label("search-input",{
						classes(BlogPageStyle.searchWrapper)
					}) {
						I({
							classes("fas","fa-search")
						})

						Input(InputType.Text) {
							classes(BlogPageStyle.searchInput)
							attr("placeholder", "Search articles...")
							id("search-input")
							value(searchQuery)
							onInput { event ->
								searchQuery = event.value
							}
						}
					}
				}

				Div({
					classes(BlogPageStyle.controlsRow)
				}) {
					if (allTags.isNotEmpty()) {
						Div({
							classes(BlogPageStyle.tagsContainer)
						}) {
							Span({
								classes(BlogPageStyle.tagLabel)
							}) {
								Text("Filter by tag:")
							}

							Span({
								classes(BlogPageStyle.tag)
								if (selectedTag == null) classes(BlogPageStyle.tagSelected)
								onClick { selectedTag = null }
							}) {
								Text("All")
							}

							allTags.forEach { (name, usages) ->
								Span({
									classes(BlogPageStyle.tag)
									if (selectedTag == name) classes(BlogPageStyle.tagSelected)
									onClick { selectedTag = name }
								}) {
									Text("$name ($usages)")
								}
							}
						}
					}

					Div({
						classes(BlogPageStyle.sortContainer)
					}) {
						Span({
							classes(BlogPageStyle.sortLabel)
						}) {
							Text("Sort by:")
						}

						Select({
							classes(BlogPageStyle.sortSelect)
							onChange {
								val selectedValue = it.value
								sortOption = SortOption.entries.find { option -> option.displayName == selectedValue }
									?: SortOption.NEWEST_FIRST
							}
						}) {
							SortOption.entries.forEach { option ->
								Option(
									value = option.displayName,
									attrs = {
										if (option == sortOption) attr("selected", "true")
									}
								) {
									Text(option.displayName)
								}
							}
						}
					}
				}
			}

			// Filter and sort articles based on search query, selected tag, and sort option
			val filteredAndSortedArticles = remember(searchQuery, selectedTag, sortOption) {
				val filtered = articlesEntries.filter { article ->
					val matchesSearch = searchQuery.isEmpty() ||
						article.title.contains(searchQuery, ignoreCase = true) ||
						article.desc.contains(searchQuery, ignoreCase = true) ||
						article.keywords.any { it.contains(searchQuery, ignoreCase = true) }

					val matchesTag = selectedTag == null || article.keywords.contains(selectedTag)

					matchesSearch && matchesTag
				}

				when (sortOption) {
					SortOption.NEWEST_FIRST -> filtered.sortedByDescending { it.date }
					SortOption.OLDEST_FIRST -> filtered.sortedBy { it.date }
					SortOption.ALPHABETICAL -> filtered.sortedBy { it.title }
				}
			}

			if (filteredAndSortedArticles.isEmpty()) {
				Div({
					classes(BlogPageStyle.noResults)
				}) {
					P {
						Text("No articles found matching your criteria. Try adjusting your search or filters.")
					}
				}
			} else {
				Div({
					classes(BlogPageStyle.resultsInfo)
				}) {
					Text("Showing ${filteredAndSortedArticles.size} article${if (filteredAndSortedArticles.size != 1) "s" else ""}")
				}

				ArticleList(entries = filteredAndSortedArticles)
			}
		}
	}
}

object BlogPageStyle : StyleSheet() {
	val container by style {
		maxWidth(900.px)
		margin(0.px, autoLength)
		padding(0.px, 1.cssRem)
	}

	val header by style {
		marginBottom(3.cssRem)
		textAlign(TextAlign.Center)
	}

	val mainTitle by style {
		fontSize(3.cssRem)
		margin(1.cssRem)
		background(linearGradient(45.deg) {
			stop(Color("#9C7CF4"))
			stop(Color("#6EBAE7"))
		})
		property("-webkit-background-clip", "text")
		property("background-clip", "text")
		property("-webkit-text-fill-color", "transparent")
	}

	val intro by style {
		fontSize(1.2.cssRem)
		lineHeight(1.6.em)
		maxWidth(700.px)
		margin(0.px, autoLength)
		color(Color("#FFFFFFDD"))
	}

	val filterSection by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.5.cssRem)
		marginBottom(2.cssRem)
	}

	val searchBox by style {
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
		width(100.percent)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val searchWrapper by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color("#FFFFFF10"))
		border(1.px, LineStyle.Solid, Color("#FFFFFF40"))
		borderRadius(2.cssRem)
		color(Color("#FFFFFFB0"))
		display(DisplayStyle.Flex)
		fontSize(1.1.cssRem)
		maxWidth(600.px)
		padding(0.8.cssRem, 1.cssRem)
		transitions {
			defaultDuration(0.3.s)
			properties("color")
		}
		width(100.percent)

		self + focusWithin style {
			color(Color.white)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val searchInput by style {
		backgroundColor(Color("transparent"))
		borderStyle(LineStyle.None)
		color("inherit")
		padding(0.px)
		outline("none")
		paddingLeft(0.75.cssRem)
		width(100.percent)

		focus style {
			outline("none")
			borderColor(Color("#FFFFFF80"))
			backgroundColor(Color("#FFFFFF15"))
			boxShadow(0.px, 0.px, 10.px, 0.px, Color("#FFFFFF20"))
		}
	}

	val controlsRow by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		justifyContent(JustifyContent.SpaceBetween)
		alignItems(AlignItems.Center)
		gap(1.5.cssRem)
	}

	val tagsContainer by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		alignItems(AlignItems.Center)
		gap(0.8.cssRem)
		marginTop(0.5.cssRem)
	}

	val tagLabel by style {
		color(Color("#FFFFFFAA"))
		alignSelf(AlignSelf.Center)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val tag by style {
		backgroundColor(Color("#FFFFFF15"))
		borderRadius(1.cssRem)
		padding(0.4.cssRem, 1.cssRem)
		fontSize(0.9.cssRem)
		color(Color("#FFFFFFDD"))
		cursor(Cursor.Pointer)

		transitions {
			"background-color" {
				duration(0.2.s)
			}
			"transform" {
				duration(0.2.s)
			}
		}

		hover style {
			backgroundColor(Color("#FFFFFF25"))
			transform {
				scale(1.05)
			}
		}
	}

	val tagSelected by style {
		backgroundColor(Color("#6EBAE750"))
		border(1.px, LineStyle.Solid, Color("#6EBAE7"))

		hover style {
			backgroundColor(Color("#6EBAE760"))
		}
	}

	val sortContainer by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(0.8.cssRem)
	}

	val sortLabel by style {
		color(Color("#FFFFFFAA"))
	}

	val sortSelect by style {
		backgroundColor(Color("#FFFFFF15"))
		color(Color.white)
		border(1.px, LineStyle.Solid, Color("#FFFFFF40"))
		borderRadius(0.5.cssRem)
		padding(0.4.cssRem, 0.8.cssRem)
		fontSize(0.9.cssRem)
		cursor(Cursor.Pointer)

		focus style {
			outline("none")
			borderColor(Color("#FFFFFF80"))
		}

		"option" {
			backgroundColor(Color("#212125"))
		}
	}

	val resultsInfo by style {
		textAlign(TextAlign.Right)
		color(Color("#FFFFFF80"))
		fontSize(0.9.cssRem)
		marginBottom(1.cssRem)
		fontStyle("italic")
	}

	val noResults by style {
		textAlign(TextAlign.Center)
		padding(3.cssRem, 1.cssRem)
		color(Color("#FFFFFFBB"))
		fontSize(1.2.cssRem)
	}

	// Responsive styles
	init {
		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			mainTitle style {
				fontSize(2.8.cssRem)
			}

			intro style {
				fontSize(1.1.cssRem)
			}

			controlsRow style {
				justifyContent(JustifyContent.Center)
				gap(1.cssRem)
			}
		}

		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			container style {
				padding(1.5.cssRem, 1.cssRem, 3.cssRem)
			}

			mainTitle style {
				fontSize(2.2.cssRem)
			}

			intro style {
				fontSize(1.cssRem)
			}

			filterSection style {
				gap(1.cssRem)
			}

			resultsInfo style {
				textAlign(TextAlign.Center)
			}
		}
	}
}
