package io.github.ayfri.pages.projects

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.AppStyle
import io.github.ayfri.AppStyle.SPECIAL_TEXT_COLOR
import io.github.ayfri.CodeTheme
import io.github.ayfri.data.DataStyle
import io.github.ayfri.data.GitHubRepository
import io.github.ayfri.data.ProjectCard
import io.github.ayfri.data.gitHubData
import io.github.ayfri.jsonld.JsonLD
import io.github.ayfri.jsonld.generateProjectsListJsonLD
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.utils.linearGradient
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
	PageLayout("Projects") {
		Style(ProjectsStyle)
		Style(DataStyle)
		Style(CodeTheme)

		rememberPageContext()
		val searchParams = URLSearchParams(window.location.search)
		val initialTagFilter = searchParams.get("tag") ?: ""
		val initialUserFilter = searchParams.get("user") ?: ""

		val allRepos = remember {
			gitHubData.repos.sortedWith(
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
					I({
						classes("fas", "fa-search", ProjectsStyle.searchIcon)
					})
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
								I({
									classes("fas", "fa-times")
								})
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
								I({
									classes("fas", "fa-times")
								})
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
								I({
									classes("fas", "fa-times")
								})
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
						I({
							classes("fas", "fa-search", ProjectsStyle.noResultsIcon)
						})
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
	const val CARD_BACKGROUND = "#ffffff08"
	const val ITEM_BACKGROUND = "#ffffff10"
	const val ITEM_BACKGROUND_HOVER = "#ffffff20"
	const val BORDER_COLOR = "#ffffff20"
	const val TEXT_SECONDARY = "#ffffffaa"
	const val ACCENT_COLOR = SPECIAL_TEXT_COLOR

	val projects by style {
		backgroundColor(Color(PROJECTS_BACKGROUND_COLOR))

		// Main background with gradient violet
		background(linearGradient(180.deg) {
			stop(Color("#0A0A0F"), (-3).percent)
			stop(Color("#1A1225"), 14.percent)
			stop(Color("#2A1B3D"), 65.percent)
			stop(Color("#1E1535"), 90.percent)
		})
	}

		val filtersSection by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(20.px)
		marginBottom(30.px)
		padding(20.px)
		borderRadius(10.px)
		border {
			width(2.px)
			style(LineStyle.Solid)
			color(Color.transparent)
		}
		property("background", "linear-gradient(#1A1225, #1A1225) padding-box, linear-gradient(45deg, #00D4FF, #FF0080) border-box")
	}

	val searchContainer by style {
		position(Position.Relative)
		width(100.percent)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val searchIcon by style {
		position(Position.Absolute)
		left(15.px)
		top(50.percent)
		transform { translateY((-50).percent) }
		color(Color("#00D4FF"))
		fontSize(1.2.cssRem)
	}

	val searchInput by style {
		width(100.percent)
		padding(15.px, 15.px, 15.px, 45.px)
		fontSize(1.1.cssRem)
		color(Color.white)
		border {
			width(2.px)
			style(LineStyle.Solid)
			color(Color.transparent)
		}
		borderRadius(8.px)
		outline("none")
		property("background", "linear-gradient(#1A1225, #1A1225) padding-box, linear-gradient(45deg, #00D4FF, #FF0080) border-box")

		self + focus style {
			property("box-shadow", "0 0 20px rgba(0, 212, 255, 0.4)")
		}
	}

	val filtersContainer by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(15.px)
		alignItems(AlignItems.Center)
	}

	val activeFilter by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(10.px)
		padding(8.px, 15.px)
		backgroundColor(Color("#00D4FF20"))
		color(Color("#00D4FF"))
		borderRadius(20.px)
		fontSize(0.9.cssRem)
		border(1.px, LineStyle.Solid, Color("#00D4FF"))
		property("box-shadow", "0 0 15px rgba(0, 212, 255, 0.3)")
	}

	val clearFilterButton by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		width(20.px)
		height(20.px)
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
		padding(8.px, 15.px)
		color(Color.white)
		border {
			width(2.px)
			style(LineStyle.Solid)
			color(Color.transparent)
		}
		borderRadius(8.px)
		cursor(Cursor.Pointer)
		outline("none")
		property("background", "linear-gradient(#1A1225, #1A1225) padding-box, linear-gradient(45deg, #00D4FF, #FF0080) border-box")

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + focus style {
			property("box-shadow", "0 0 15px rgba(255, 0, 128, 0.4)")
		}

		"option" {
			backgroundColor(Color(PROJECTS_BACKGROUND_COLOR))
			color(Color.white)
		}
	}

	val popularTags by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(10.px)
		marginTop(10.px)
	}

	val popularTagsLabel by style {
		fontSize(0.9.cssRem)
		color(Color(TEXT_SECONDARY))
	}

	val tagsList by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(10.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val tagButton by style {
		padding(5.px, 15.px)
		color(Color.white)
		border {
			width(2.px)
			style(LineStyle.Solid)
			color(Color.transparent)
		}
		borderRadius(20.px)
		fontSize(0.9.cssRem)
		cursor(Cursor.Pointer)
		property("background", "linear-gradient(#1A1225, #1A1225) padding-box, linear-gradient(45deg, #00D4FF, #FF0080) border-box")

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		hover(self) style {
			property("background", "linear-gradient(#252525, #252525) padding-box, linear-gradient(45deg, #00D4FF, #FF0080) border-box")
			transform { scale(1.05) }
			property("box-shadow", "0 0 15px rgba(0, 212, 255, 0.3)")
		}
	}

	val activeTagButton by style {
		backgroundColor(Color("#00D4FF20"))
		color(Color("#00D4FF"))
		border(1.px, LineStyle.Solid, Color("#00D4FF"))
		property("box-shadow", "0 0 15px rgba(255, 0, 128, 0.3)")

		self + hover style {
			backgroundColor(Color("#FF008030"))
			property("box-shadow", "0 0 20px rgba(255, 0, 128, 0.5)")
		}
	}

	val resultsCount by style {
		marginBottom(20.px)
		fontSize(0.9.cssRem)
		color(Color(TEXT_SECONDARY))
		textAlign(TextAlign.Right)
	}

	val noResults by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		gap(15.px)
		padding(50.px)
		gridColumn("1 / -1")
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
		marginBottom(10.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val resetButton by style {
		marginTop(10.px)
		padding(10.px, 20.px)
		backgroundColor(Color("#00D4FF20"))
		color(Color("#00D4FF"))
		border(1.px, LineStyle.Solid, Color("#00D4FF"))
		borderRadius(8.px)
		fontSize(1.cssRem)
		cursor(Cursor.Pointer)

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + hover style {
			backgroundColor(Color("#FF008030"))
			transform { scale(1.05) }
			property("box-shadow", "0 0 20px rgba(255, 0, 128, 0.4)")
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
