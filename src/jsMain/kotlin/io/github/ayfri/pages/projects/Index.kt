package io.github.ayfri.pages.projects

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.AppStyle
import io.github.ayfri.CodeTheme
import io.github.ayfri.components.ChipGroup
import io.github.ayfri.components.FiltersStyle
import io.github.ayfri.components.SearchField
import io.github.ayfri.components.countedByFrequency
import io.github.ayfri.data.DataStyle
import io.github.ayfri.data.GitHubRepository
import io.github.ayfri.data.ProjectCard
import io.github.ayfri.data.portfolioData
import io.github.ayfri.jsonld.PageType
import io.github.ayfri.jsonld.projectsListNode
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.pages.IconHeading
import io.github.ayfri.pages.IconStyle
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.url.URLSearchParams

@Page
@Composable
fun Projects() {
	val portfolio = portfolioData()
	val allRepos = remember(portfolio) {
		portfolio.repos.sortedWith(
			compareByDescending(GitHubRepository::stargazersCount)
				.thenBy(String.CASE_INSENSITIVE_ORDER, GitHubRepository::fullName)
		)
	}

	PageLayout(
		"Projects",
		description = "Open-source projects by Pierre Roy (Ayfri) on GitHub: Kotlin libraries, Minecraft tools, web apps, and more, searchable by language and tag.",
		schemaType = PageType.CollectionPage,
		jsonLD = arrayOf(projectsListNode(allRepos)),
	) {
		Style(ProjectsStyle)
		Style(IconStyle)
		Style(DataStyle)
		Style(CodeTheme)

		rememberPageContext()
		val searchParams = URLSearchParams(window.location.search)

		val allTags = remember(allRepos) { allRepos.flatMap { it.topics.toList() }.countedByFrequency() }
		val allLanguages = remember(allRepos) { allRepos.mapNotNull { it.language }.countedByFrequency() }
		val allUsers = remember(allRepos) { allRepos.map { it.owner.login }.countedByFrequency() }

		var searchQuery by remember { mutableStateOf("") }
		var tagFilter by remember { mutableStateOf(searchParams.get("tag") ?: "") }
		var selectedLanguage by remember { mutableStateOf("") }
		var selectedUser by remember { mutableStateOf(searchParams.get("user") ?: "") }

		val filteredRepos = remember(allRepos, searchQuery, tagFilter, selectedLanguage, selectedUser) {
			allRepos.filter { repo ->
				(searchQuery.isEmpty() || repo.name.contains(searchQuery, true) || repo.description?.contains(searchQuery, true) == true) &&
					(tagFilter.isEmpty() || tagFilter in repo.topics) &&
					(selectedLanguage.isEmpty() || repo.language == selectedLanguage) &&
					(selectedUser.isEmpty() || repo.owner.login == selectedUser)
			}
		}

		/** The first run only normalizes the landing URL, so it replaces the history entry instead of needing a second Back press. */
		val isFirstRun = remember { booleanArrayOf(true) }
		LaunchedEffect(tagFilter, selectedUser) {
			val query = listOf("tag" to tagFilter, "user" to selectedUser)
				.filter { it.second.isNotEmpty() }
				.joinToString("&") { (key, value) -> "$key=$value" }
			val url = window.location.pathname + if (query.isEmpty()) "" else "?$query"
			if (isFirstRun[0]) window.history.replaceState(null, "", url) else window.history.pushState(null, "", url)
			isFirstRun[0] = false
		}

		Div({
			classes(AppStyle.sections)
		}) {
			H1({
				classes(AppStyle.title)
			}) {
				Text("Projects")
			}

			Div({
				classes(AppStyle.card, ProjectsStyle.filters)
			}) {
				Div({
					classes(ProjectsStyle.searchRow)
				}) {
					SearchField(searchQuery, "Search projects") { searchQuery = it }

					Select({
						classes(AppStyle.input)
						attr("aria-label", "Language")
						onChange { selectedLanguage = it.target.value }
					}) {
						Option("") { Text("All languages") }

						allLanguages.forEach { (language, count) ->
							Option(language, {
								if (language == selectedLanguage) selected()
							}) {
								Text("$language ($count)")
							}
						}
					}
				}

				ChipGroup("Popular tags", { LucideTags() }, allTags.take(10), tagFilter) { tagFilter = it }
				ChipGroup("Users and organizations", { LucideUsers() }, allUsers, selectedUser) { selectedUser = it }
			}

			P({
				classes(FiltersStyle.label, ProjectsStyle.resultsCount)
			}) {
				Span({ classes(IconStyle.label) }) {
					LucideFunnel()
					Text("Showing ${filteredRepos.size} of ${allRepos.size} projects")
				}
			}

			Section({
				classes(ProjectsStyle.projectsList)
			}) {
				if (filteredRepos.isEmpty()) Div({
					classes(ProjectsStyle.noResults)
				}) {
					IconHeading({ LucideSearch() }, "No project matches these filters")

					Button({
						classes(AppStyle.button)
						onClick {
							searchQuery = ""
							tagFilter = ""
							selectedLanguage = ""
							selectedUser = ""
						}
					}) {
						LucideRotateCcw()
						Text("Reset all filters")
					}
				} else filteredRepos.forEach { ProjectCard(it) }
			}
		}
	}
}

object ProjectsStyle : StyleSheet() {
	val filters by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.25.cssRem)
		padding(1.25.cssRem)
	}

	val searchRow by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.75.cssRem)
	}

	val resultsCount by style {
		margin(1.25.cssRem, 0.px)
		textAlign(TextAlign.Right)
	}

	val noResults by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		gridColumn(1, -1)
		padding(3.cssRem)

		"h2" {
			fontSize(1.3.cssRem)
			margin(0.px)
		}
	}

	val projectsList by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns {
			repeat(GridEntry.Repeat.Auto.Type.AutoFill) {
				minmax(25.cssRem, 1.fr)
			}
		}
		gap(1.5.cssRem)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)
			}
		}
	}
}
