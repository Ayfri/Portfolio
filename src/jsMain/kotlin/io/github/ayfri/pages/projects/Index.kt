package io.github.ayfri.pages.projects

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.AppStyle
import io.github.ayfri.CodeTheme
import io.github.ayfri.components.FontAwesomeType
import io.github.ayfri.components.I
import io.github.ayfri.data.DataStyle
import io.github.ayfri.data.GitHubRepository
import io.github.ayfri.data.ProjectCard
import io.github.ayfri.data.portfolioData
import io.github.ayfri.jsonld.JsonLD
import io.github.ayfri.jsonld.generateProjectsListJsonLD
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.utils.gradientBorderBackground
import io.github.ayfri.utils.pageBackground
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.url.URLSearchParams

@Page
@Composable
fun Projects() {
	PageLayout(
		"Projects",
		description = "Open-source projects by Pierre Roy (Ayfri) on GitHub: Kotlin libraries, Minecraft tools, web apps, and more, searchable by language and tag.",
		keywords = "GitHub projects, open source Kotlin, Minecraft tools, Ayfri GitHub, developer portfolio projects",
	) {
		val portfolio = portfolioData()

		Style(ProjectsStyle)
		Style(DataStyle)
		Style(CodeTheme)

		rememberPageContext()
		val searchParams = URLSearchParams(window.location.search)
		val initialTagFilter = searchParams.get("tag") ?: ""
		val initialUserFilter = searchParams.get("user") ?: ""

		val allRepos = remember(portfolio) {
			portfolio.repos.sortedWith(
				compareByDescending(GitHubRepository::stargazersCount)
					.thenBy(String.CASE_INSENSITIVE_ORDER, GitHubRepository::fullName)
			)
		}

		var searchQuery by remember { mutableStateOf("") }
		var tagFilter by remember { mutableStateOf(initialTagFilter) }
		var selectedLanguage by remember { mutableStateOf("") }
		var selectedUser by remember { mutableStateOf(initialUserFilter) }

		// Extract all unique tags, languages, and users with counts
		val allTags = remember {
			allRepos.flatMap { it.topics.toList() }
				.groupBy { it }
				.map { it.key to it.value.size }
				.sortedByDescending { it.second }
		}

		val allLanguages = remember {
			allRepos.mapNotNull { it.language }
				.groupBy { it }
				.map { it.key to it.value.size }
				.sortedByDescending { it.second }
		}

		val allUsers = remember {
			allRepos.map { it.owner.login }
				.groupBy { it }
				.map { it.key to it.value.size }
				.sortedByDescending { it.second }
		}

		// Filter repositories based on search, tag, language, and user
		val filteredRepos = remember(searchQuery, tagFilter, selectedLanguage, selectedUser) {
			allRepos.filter { repo ->
				val matchesSearch = searchQuery.isEmpty() ||
					repo.name.contains(searchQuery, ignoreCase = true) ||
					repo.description?.contains(searchQuery, ignoreCase = true) == true

				val matchesTag = tagFilter.isEmpty() || repo.topics.contains(tagFilter)

				val matchesLanguage = selectedLanguage.isEmpty() || repo.language == selectedLanguage

				val matchesUser = selectedUser.isEmpty() || repo.owner.login == selectedUser

				matchesSearch && matchesTag && matchesLanguage && matchesUser
			}
		}

		// Add JSON-LD
		val jsonLd = generateProjectsListJsonLD(allRepos)
		JsonLD(jsonLd)

		// Update URL when filters change
		LaunchedEffect(tagFilter, selectedUser) {
			val params = mutableListOf<String>()

			if (tagFilter.isNotEmpty()) {
				params += "tag=$tagFilter"
			}

			if (selectedUser.isNotEmpty()) {
				params += "user=$selectedUser"
			}

			val queryString = if (params.isNotEmpty()) "?${params.joinToString("&")}" else ""
			window.history.pushState(null, "", "${window.location.pathname}$queryString")
		}

		Div({
			classes(AppStyle.sections, ProjectsStyle.projects)
		}) {
			H1({
				classes(AppStyle.monoFont, AppStyle.title)
			}) {
				Span {
					Text("My Projects")
				}
			}

			// Filters section
			Div({
				classes(ProjectsStyle.filtersSection)
			}) {
				// Search input
				Div({
					classes(ProjectsStyle.searchContainer)
				}) {
					I(FontAwesomeType.SOLID, "search", ProjectsStyle.searchIcon)
					Input(InputType.Text) {
						classes(ProjectsStyle.searchInput)
						placeholder("Search projects...")
						value(searchQuery)
						onInput { event -> searchQuery = event.value }
					}
				}

				// Filters
				Div({
					classes(ProjectsStyle.filtersContainer)
				}) {
					// Active tag filter
					if (tagFilter.isNotEmpty()) {
						Div({
							classes(ProjectsStyle.activeFilter)
						}) {
							Text("Tag: $tagFilter (${allTags.find { it.first == tagFilter }?.second ?: 0})")
							Button({
								classes(ProjectsStyle.clearFilterButton)
								onClick { tagFilter = "" }
							}) {
								I(FontAwesomeType.SOLID, "times")
							}
						}
					}

					// Active language filter
					if (selectedLanguage.isNotEmpty()) {
						Div({
							classes(ProjectsStyle.activeFilter)
						}) {
							Text("Language: $selectedLanguage (${allLanguages.find { it.first == selectedLanguage }?.second ?: 0})")
							Button({
								classes(ProjectsStyle.clearFilterButton)
								onClick { selectedLanguage = "" }
							}) {
								I(FontAwesomeType.SOLID, "times")
							}
						}
					}

					// Active user filter
					if (selectedUser.isNotEmpty()) {
						Div({
							classes(ProjectsStyle.activeFilter)
						}) {
							Text("User: $selectedUser (${allUsers.find { it.first == selectedUser }?.second ?: 0})")
							Button({
								classes(ProjectsStyle.clearFilterButton)
								onClick { selectedUser = "" }
							}) {
								I(FontAwesomeType.SOLID, "times")
							}
						}
					}

					// Language dropdown
					if (allLanguages.isNotEmpty()) {
						Div({
							classes(ProjectsStyle.filterDropdown)
						}) {
							Select({
								classes(ProjectsStyle.filterSelect)
								onChange { event -> selectedLanguage = event.target.value }
							}) {
								Option("") {
									Text("All Languages")
								}

								allLanguages.forEach { (language, count) ->
									Option(language, {
										if (language == selectedLanguage) {
											selected()
										}
									}) {
										Text("$language ($count)")
									}
								}
							}
						}
					}
				}

				// Popular tags
				if (allTags.isNotEmpty()) {
					Div({
						classes(ProjectsStyle.popularTags)
					}) {
						Span({
							classes(ProjectsStyle.popularTagsLabel)
						}) {
							Text("Popular tags:")
						}

						Div({
							classes(ProjectsStyle.tagsList)
						}) {
							allTags.take(10).forEach { (tag, count) ->
								Button({
									classes(ProjectsStyle.tagButton)
									if (tag == tagFilter) {
										classes(ProjectsStyle.activeTagButton)
									}
									onClick {
										tagFilter = if (tagFilter == tag) "" else tag
									}
								}) {
									Text("$tag ($count)")
								}
							}
						}
					}
				}

				// Popular users
				if (allUsers.isNotEmpty()) {
					Div({
						classes(ProjectsStyle.popularTags)
					}) {
						Span({
							classes(ProjectsStyle.popularTagsLabel)
						}) {
							Text("Users/Organizations:")
						}

						Div({
							classes(ProjectsStyle.tagsList)
						}) {
							allUsers.forEach { (user, count) ->
								Button({
									classes(ProjectsStyle.tagButton)
									if (user == selectedUser) {
										classes(ProjectsStyle.activeTagButton)
									}
									onClick {
										selectedUser = if (selectedUser == user) "" else user
									}
								}) {
									Text("$user ($count)")
								}
							}
						}
					}
				}
			}

			// Results count
			Div({
				classes(ProjectsStyle.resultsCount)
			}) {
				Text("Showing ${filteredRepos.size} of ${allRepos.size} projects")
			}

			Section({
				classes(ProjectsStyle.projectsList)
			}) {
				if (filteredRepos.isEmpty()) {
					Div({
						classes(ProjectsStyle.noResults)
					}) {
						I(FontAwesomeType.SOLID, "search", ProjectsStyle.noResultsIcon)
						H2 {
							Text("No projects found")
						}
						P {
							Text("Try adjusting your search or filters")
						}
						Button({
							classes(ProjectsStyle.resetButton)
							onClick {
								searchQuery = ""
								tagFilter = ""
								selectedLanguage = ""
								selectedUser = ""
							}
						}) {
							Text("Reset all filters")
						}
					}
				} else {
					filteredRepos.forEach { repository ->
						ProjectCard(repository)
					}
				}
			}
		}
	}
}

object ProjectsStyle : StyleSheet() {
	// Color constants
	const val PROJECTS_BACKGROUND_COLOR = "#1A1225"
	const val TEXT_SECONDARY = "#ffffffaa"

	val projects by style {
		pageBackground()
	}

	val filtersSection by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.25.cssRem)
		marginBottom(1.875.cssRem)
		padding(1.25.cssRem)
		borderRadius(.625.cssRem)
		border {
			width(2.px)
			style(LineStyle.Solid)
			color(Color.transparent)
		}
		gradientBorderBackground(Color(PROJECTS_BACKGROUND_COLOR))
	}

	val searchContainer by style {
		position(Position.Relative)
		width(100.percent)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val searchIcon by style {
		position(Position.Absolute)
		left(.9375.cssRem)
		top(50.percent)
		transform { translateY((-50).percent) }
		color(Color("#00D4FF"))
		fontSize(1.2.cssRem)
	}

	val searchInput by style {
		border(2.px, LineStyle.Solid, Color.transparent)
		gradientBorderBackground(Color(PROJECTS_BACKGROUND_COLOR))
		borderRadius(.5.cssRem)
		color(Color.white)
		fontSize(1.1.cssRem)
		outlineStyle(LineStyle.None)
		padding(.9375.cssRem, .9375.cssRem, .9375.cssRem, 2.8125.cssRem)
		width(100.percent)

		self + focus style {
			boxShadow("0 0 20px rgba(0, 212, 255, 0.4)")
		}
	}

	val filtersContainer by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.9375.cssRem)
		alignItems(AlignItems.Center)
	}

	val activeFilter by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(.625.cssRem)
		padding(.5.cssRem, .9375.cssRem)
		backgroundColor(Color("#00D4FF20"))
		color(Color("#00D4FF"))
		borderRadius(1.25.cssRem)
		fontSize(0.9.cssRem)
		border(1.px, LineStyle.Solid, Color("#00D4FF"))
		boxShadow("0 0 15px rgba(0, 212, 255, 0.3)")
	}

	val clearFilterButton by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		width(1.25.cssRem)
		height(1.25.cssRem)
		padding(0.px)
		backgroundColor(Color.transparent)
		color(Color("#00D4FF"))
		border(0.px)
		borderRadius(50.percent)
		cursor(Cursor.Pointer)

		self + hover style {
			backgroundColor(Color("#FF008040"))
		}
	}

	val filterDropdown by style {
		position(Position.Relative)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val filterSelect by style {
		padding(.5.cssRem, .9375.cssRem)
		color(Color.white)
		border(2.px, LineStyle.Solid, Color.transparent)
		borderRadius(.5.cssRem)
		cursor(Cursor.Pointer)
		outline("none")
		gradientBorderBackground(Color(PROJECTS_BACKGROUND_COLOR))

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + focus style {
			boxShadow("0 0 15px rgba(255, 0, 128, 0.4)")
		}

		"option" {
			backgroundColor(Color(PROJECTS_BACKGROUND_COLOR))
			color(Color.white)
		}
	}

	val popularTags by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(.625.cssRem)
		marginTop(.625.cssRem)
	}

	val popularTagsLabel by style {
		fontSize(0.9.cssRem)
		color(Color(TEXT_SECONDARY))
	}

	val tagsList by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.625.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val tagButton by style {
		padding(.3125.cssRem, .9375.cssRem)
		color(Color.white)
		border(2.px, LineStyle.Solid, Color.transparent)
		borderRadius(1.25.cssRem)
		fontSize(0.9.cssRem)
		cursor(Cursor.Pointer)
		gradientBorderBackground(Color(PROJECTS_BACKGROUND_COLOR))

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		hover(self) style {
			gradientBorderBackground(Color("#252525"))
			boxShadow("0 0 15px rgba(0, 212, 255, 0.3)")
			transform { scale(1.05) }
		}
	}

	val activeTagButton by style {
		backgroundColor(Color("#00D4FF20"))
		color(Color("#00D4FF"))
		border(1.px, LineStyle.Solid, Color("#00D4FF"))
		boxShadow("0 0 15px rgba(255, 0, 128, 0.3)")

		self + hover style {
			backgroundColor(Color("#FF008030"))
			boxShadow("0 0 20px rgba(255, 0, 128, 0.5)")
		}
	}

	val resultsCount by style {
		marginBottom(1.25.cssRem)
		fontSize(0.9.cssRem)
		color(Color(TEXT_SECONDARY))
		textAlign(TextAlign.Right)
	}

	val noResults by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		gap(.9375.cssRem)
		padding(3.125.cssRem)
		gridColumn(1, -1)
		textAlign(TextAlign.Center)

		"h2" style {
			margin(0.px)
			fontSize(1.8.cssRem)
			color(Color("#ffffffdd"))
		}

		"p" style {
			margin(0.px)
			color(Color(TEXT_SECONDARY))
		}
	}

	val noResultsIcon by style {
		fontSize(3.cssRem)
		color(Color("#00D4FF50"))
		marginBottom(.625.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val resetButton by style {
		marginTop(.625.cssRem)
		padding(.625.cssRem, 1.25.cssRem)
		backgroundColor(Color("#00D4FF20"))
		color(Color("#00D4FF"))
		border(1.px, LineStyle.Solid, Color("#00D4FF"))
		borderRadius(.5.cssRem)
		fontSize(1.cssRem)
		cursor(Cursor.Pointer)

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + hover style {
			backgroundColor(Color("#FF008030"))
			boxShadow("0 0 20px rgba(255, 0, 128, 0.4)")
			transform { scale(1.05) }
		}
	}

	val projectsList by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns {
			repeat(GridEntry.Repeat.Auto.Type.AutoFill) {
				minmax(25.cssRem, 1.fr)
			}
		}
		gap(1.75.cssRem)
		padding(0.px)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)
			}
		}
	}
}
