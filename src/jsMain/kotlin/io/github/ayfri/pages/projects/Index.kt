package io.github.ayfri.pages.projects

import JsonLD
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
import io.github.ayfri.jsonld.generateProjectsListJsonLD
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.utils.minmax
import io.github.ayfri.utils.repeat
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.url.URLSearchParams

@Page
@Composable
fun Projects() {
	PageLayout("Projects") {
		Style(ProjectsStyle)
		Style(DataStyle)
		Style(CodeTheme)

		val context = rememberPageContext()
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
			allRepos.flatMap { it.topics }
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
				Text("My Projects")
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
						ProjectCard(repository) onClick@{
							context.router.navigateTo("/projects/${repository.fullName}")

							val list = document.querySelector(".${ProjectsStyle.projectsList}").unsafeCast<HTMLElement>()
							val style = window.getComputedStyle(list)
							val rowsCount = style.getPropertyValue("grid-template-columns").split(" ").size

							list.style.setProperty("--${DataStyle.gridColumnStartVar.name}", rowsCount.toString())
						}
					}
				}
			}
		}
	}
}

object ProjectsStyle : StyleSheet() {
	// Color constants
	const val PROJECTS_BACKGROUND_COLOR = "#15151C"
	const val CARD_BACKGROUND = "#ffffff08"
	const val ITEM_BACKGROUND = "#ffffff10"
	const val ITEM_BACKGROUND_HOVER = "#ffffff20"
	const val BORDER_COLOR = "#ffffff20"
	const val TEXT_SECONDARY = "#ffffffaa"
	const val ACCENT_COLOR = SPECIAL_TEXT_COLOR

	val projects by style {
		backgroundColor(Color(PROJECTS_BACKGROUND_COLOR))
	}

	val filtersSection by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(20.px)
		marginBottom(30.px)
		padding(20.px)
		background(Color(CARD_BACKGROUND))
		borderRadius(10.px)
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
		color(Color(ACCENT_COLOR))
		fontSize(1.2.cssRem)
	}

	val searchInput by style {
		width(100.percent)
		padding(15.px, 15.px, 15.px, 45.px)
		fontSize(1.1.cssRem)
		backgroundColor(Color(ITEM_BACKGROUND))
		color(Color.white)
		border(0.px)
		borderRadius(8.px)
		outline("none")

		self + focus style {
			boxShadow(0.px, 0.px, 0.px, 2.px, Color(ACCENT_COLOR + "40"))
		}
	}

	val filtersContainer by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(15.px)
		alignItems(org.jetbrains.compose.web.css.AlignItems.Center)
	}

	val activeFilter by style {
		display(DisplayStyle.Flex)
		alignItems(org.jetbrains.compose.web.css.AlignItems.Center)
		gap(10.px)
		padding(8.px, 15.px)
		backgroundColor(Color(ACCENT_COLOR + "20"))
		color(Color(ACCENT_COLOR))
		borderRadius(20.px)
		fontSize(0.9.cssRem)
	}

	val clearFilterButton by style {
		display(DisplayStyle.Flex)
		alignItems(org.jetbrains.compose.web.css.AlignItems.Center)
		justifyContent(org.jetbrains.compose.web.css.JustifyContent.Center)
		width(20.px)
		height(20.px)
		padding(0.px)
		backgroundColor(Color.transparent)
		color(Color(ACCENT_COLOR))
		border(0.px)
		borderRadius(50.percent)
		cursor(Cursor.Pointer)

		self + hover style {
			backgroundColor(Color(ACCENT_COLOR + "40"))
		}
	}

	val filterDropdown by style {
		position(Position.Relative)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val filterSelect by style {
		padding(8.px, 15.px)
		backgroundColor(Color(ITEM_BACKGROUND))
		color(Color.white)
		border(1.px, LineStyle.Solid, Color(BORDER_COLOR))
		borderRadius(8.px)
		cursor(Cursor.Pointer)
		outline("none")

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + focus style {
			borderColor(Color(ACCENT_COLOR))
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
		backgroundColor(Color(ITEM_BACKGROUND))
		color(Color.white)
		border(0.px)
		borderRadius(20.px)
		fontSize(0.9.cssRem)
		cursor(Cursor.Pointer)

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + hover style {
			backgroundColor(Color(ITEM_BACKGROUND_HOVER))
			transform { scale(1.05) }
		}
	}

	val activeTagButton by style {
		backgroundColor(Color(ACCENT_COLOR + "20"))
		color(Color(ACCENT_COLOR))

		self + hover style {
			backgroundColor(Color(ACCENT_COLOR + "30"))
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
		alignItems(org.jetbrains.compose.web.css.AlignItems.Center)
		justifyContent(org.jetbrains.compose.web.css.JustifyContent.Center)
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
		color(Color(ACCENT_COLOR + "50"))
		marginBottom(10.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val resetButton by style {
		marginTop(10.px)
		padding(10.px, 20.px)
		backgroundColor(Color(ACCENT_COLOR + "20"))
		color(Color(ACCENT_COLOR))
		border(0.px)
		borderRadius(8.px)
		fontSize(1.cssRem)
		cursor(Cursor.Pointer)

		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + hover style {
			backgroundColor(Color(ACCENT_COLOR + "30"))
			transform { scale(1.05) }
		}
	}

	val projectsList by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns(repeat("auto-fill", minmax(25.cssRem, 1.fr)))
		gap(2.cssRem)
		padding(0.px)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)
			}
		}
	}
}
