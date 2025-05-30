package io.github.ayfri.pages.projects

import JsonLD
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.AppStyle
import io.github.ayfri.AppStyle.LINK_HOVER_COLOR
import io.github.ayfri.AppStyle.SPECIAL_TEXT_COLOR
import io.github.ayfri.CodeTheme
import io.github.ayfri.components.*
import io.github.ayfri.data.gitHubData
import io.github.ayfri.ensureSuffix
import io.github.ayfri.jsonld.generateProjectJsonLD
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.markdownParagraph
import io.github.ayfri.utils.Overflow
import io.github.ayfri.utils.overflow
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.Nth
import org.jetbrains.compose.web.dom.*
import web.location.location
import kotlin.js.Date

@Page("{user}/{project}")
@Composable
fun All() {
	Style(ProjectStyle)

	val context = rememberPageContext()
	val userName = context.route.params["user"]
	val projectName = context.route.params["project"]

	if (projectName == null || userName == null) {
		location.href = "/projects/"
		return
	}

	val projects = gitHubData.repos
	val project = projects.find { it.fullName == "$userName/$projectName" }

	if (project == null) {
		location.href = "/projects/"
		return
	}

	val path = context.route.toString()
	setTitle("${project.name} by ${project.owner.login} - ${AppGlobals["author"]}'s Portfolio")
	setDescription(project.description ?: AppGlobals["description"]!!)
	setCanonical(AppGlobals["url"] + path.ensureSuffix("/"))

	LaunchedEffect(project.fullName) {
		js("window.Prism.highlightAll()").unsafeCast<Unit>()
	}

	PageLayout(project.name) {
		// Add JSON-LD
		val jsonLd = generateProjectJsonLD(project, path)
		JsonLD(jsonLd)

		Div({
			classes(ProjectStyle.content)
		}) {
			// Breadcrumb Navigation
			Div({
				classes(ProjectStyle.breadcrumbs)
			}) {
				A("/", {
					classes(ProjectStyle.breadcrumbLink)
				}) {
					I({
						classes("fas", "fa-home")
					})
					Text(" Home")
				}

				Span({
					classes(ProjectStyle.breadcrumbSeparator)
				}) {
					I({
						classes("fas", "fa-chevron-right")
					})
				}

				A("/projects/", {
					classes(ProjectStyle.breadcrumbLink)
				}) {
					Text("Projects")
				}

				Span({
					classes(ProjectStyle.breadcrumbSeparator)
				}) {
					I({
						classes("fas", "fa-chevron-right")
					})
				}

				A("/projects/?user=${project.owner.login}", {
					classes(ProjectStyle.breadcrumbLink)
				}) {
					Text(project.owner.login)
				}

				Span({
					classes(ProjectStyle.breadcrumbSeparator)
				}) {
					I({
						classes("fas", "fa-chevron-right")
					})
				}

				Span({
					classes(ProjectStyle.breadcrumbCurrent)
				}) {
					Text(project.name)
				}
			}

			// Project Header
			Div({
				classes(ProjectStyle.header)
			}) {
				H1({
					classes(ProjectStyle.title)
				}) {
					Img(project.owner.avatarUrl, project.owner.login)
					Text(project.name)
					Span(" by ${project.owner.login}")
				}

				P(project.description ?: "No description provided.", ProjectStyle.description)

				// Quick Stats
				Div({
					classes(ProjectStyle.quickStats)
				}) {
					QuickStat(project.language ?: "Unknown", "code")
					QuickStat("${project.stargazersCount} stars", "star")
					QuickStat("${project.forksCount} forks", "code-branch")
					QuickStat(formatDate(project.updatedAt), "calendar-alt")
				}

				// Project Topics
				if (project.topics.isNotEmpty()) {
					Div({
						classes(ProjectStyle.topicsContainer, ProjectStyle.headerTopics)
					}) {
						Div({
							classes(ProjectStyle.topicsList)
						}) {
							project.topics.forEach { topic ->
								A("/projects/?tag=${topic}", {
									classes(ProjectStyle.topicTag)
								}) {
									Text(topic)
								}
							}
						}
					}
				}
			}

			// Main Content Layout
			Div({
				classes(ProjectStyle.mainLayout)
			}) {
				// Left Column - README
				Div({
					classes(ProjectStyle.readmeSection)
				}) {

					// Fix images/links, prepend with base url if they don't have a protocol
					val baseUrl = "${project.htmlUrl}/raw/${project.defaultBranch}/"
					val readmeContent = project.readmeContent?.replace(Regex("""(src|href)="([^"]+?)"""")) { match ->
						val (attr, url) = match.destructured
						if (url.startsWith("http://") || url.startsWith("https://")) {
							"$attr=\"$url\""
						} else {
							"$attr=\"$baseUrl$url\""
						}
					}

					Div {
						Style(CodeTheme)
						P({
							markdownParagraph(readmeContent ?: "No README provided.", false, ProjectStyle.readme)
						})
					}
				}

				// Right Column - Project Info
				Div({
					classes(ProjectStyle.sidebarSection)
				}) {
					// Project Links
					Div({
						classes(ProjectStyle.infoCard)
					}) {
						H3 {
							I({
								classes("fas", "fa-link")
							})
							Text(" Links")
						}

						Div({
							classes(ProjectStyle.linksList)
						}) {
							A(project.htmlUrl, {
								classes(ProjectStyle.linkItem)
								target(ATarget.Blank)
							}) {
								I({
									classes("fab", "fa-github")
								})
								Span("GitHub Repository")
							}

							project.homepage?.let {
								A(it, {
									classes(ProjectStyle.linkItem)
									target(ATarget.Blank)
								}) {
									I({
										classes("fas","fa-globe")
									})

									Span("Project Homepage")
								}
							}

							// Add download link
							A("${project.htmlUrl}/archive/refs/heads/${project.defaultBranch}.zip", {
								classes(ProjectStyle.linkItem)
								target(ATarget.Blank)
							}) {
								I({
									classes("fas", "fa-download")
								})
								Span("Download ZIP")
							}
						}
					}

					// Project Stats
					Div({
						classes(ProjectStyle.infoCard)
					}) {
						H3 {
							I({
								classes("fas", "fa-chart-bar")
							})
							Text(" Statistics")
						}

						Div({
							classes(ProjectStyle.statsGrid)
						}) {
							StatCard("Stars", project.stargazersCount.toString(), "star")
							StatCard("Forks", project.forksCount.toString(), "code-branch")
							StatCard("Watchers", project.watchersCount.toString(), "eye")
							StatCard("Issues", project.openIssuesCount.toString(), "exclamation-circle")
							StatCard("Commits", project.commitsCount.toString(), "code-commit")
							StatCard("Contributors", project.contributorsCount.toString(), "users")
							StatCard("Size", "${project.size} KB", "file")
						}
					}

					// Project Timeline
					Div({
						classes(ProjectStyle.infoCard)
					}) {
						H3 {
							I({
								classes("fas", "fa-history")
							})
							Text(" Timeline")
						}

						Div({
							classes(ProjectStyle.datesList)
						}) {
							DateItem("Created", formatDate(project.createdAt))
							DateItem("Last Updated", formatDate(project.updatedAt))
							DateItem("Last Push", formatDate(project.pushedAt))
						}
					}

					// Project Status
					Div({
						classes(ProjectStyle.infoCard)
					}) {
						H3 {
							I({
								classes("fas", "fa-info-circle")
							})
							Text(" Status")
						}

						Div({
							classes(ProjectStyle.statusList)
						}) {
							StatusItem("Visibility", project.visibility.capitalize())
							StatusItem("Template", if (project.isTemplate) "Yes" else "No")
							StatusItem("Archived", if (project.archived) "Yes" else "No")
							StatusItem("Fork", if (project.fork) "Yes" else "No")
						}
					}
				}
			}
		}
	}
}

@Composable
private fun QuickStat(value: String, iconName: String) {
	Div({
		classes(ProjectStyle.quickStatItem)
	}) {
		I({
			classes("fas", "fa-$iconName")
		})
		Text(" $value")
	}
}

@Composable
private fun StatCard(label: String, value: String, iconName: String) {
	Div({
		classes(ProjectStyle.statCard)
	}) {
		Div({
			classes(ProjectStyle.statIcon)
		}) {
			I({
				classes("fas", "fa-${iconName}")
			})
		}
		Div({
			classes(ProjectStyle.statContent)
		}) {
			Span({
				classes(ProjectStyle.statValue)
			}) {
				Text(value)
			}
			Span({
				classes(ProjectStyle.statLabel)
			}) {
				Text(label)
			}
		}
	}
}

@Composable
private fun DateItem(label: String, date: String) {
	Div({
		classes(ProjectStyle.dateItem)
	}) {
		I({
			classes("fas", "fa-calendar-alt")
		})
		Span({
			classes(ProjectStyle.dateLabel)
		}) {
			Text(label)
		}
		Span({
			classes(ProjectStyle.dateValue)
		}) {
			Text(date)
		}
	}
}

@Composable
private fun StatusItem(label: String, value: String) {
	Div({
		classes(ProjectStyle.statusItem)
	}) {
		I({
			classes("fas", "fa-info-circle")
		})
		Span({
			classes(ProjectStyle.statusLabel)
		}) {
			Text(label)
		}
		Span({
			classes(ProjectStyle.statusValue)
		}) {
			Text(value)
		}
	}
}

private fun formatDate(dateString: String): String {
	val date = Date(dateString)
	return date.toLocaleDateString()
}

private fun String.capitalize(): String {
	return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

object ProjectStyle : StyleSheet() {
	// Color constants
	const val CARD_BACKGROUND = "#ffffff10"
	const val ITEM_BACKGROUND = "#ffffff15"
	const val ITEM_BACKGROUND_HOVER = "#ffffff25"
	const val BORDER_COLOR = "#ffffff20"
	const val TEXT_SECONDARY = "#ffffffaa"
	const val ACCENT_COLOR = SPECIAL_TEXT_COLOR

	init {
		group(type("td"), type("th")) style {
			borderCollapse(BorderCollapse.Collapse)
			padding(10.px)
		}

		child(type("thead"), type("tr")) style {
			background(Color("#ffffff20"))
		}

		"table" style {
			borderRadius(10.px)
			overflow(Overflow.Hidden)
			borderCollapse(BorderCollapse.Collapse)
		}

		type("tr") + nthChild(Nth.Even) style {
			background(Color("#ffffff0b"))
		}

		"pre" {
			borderRadius(10.px)
		}

		"h3" style {
			margin(0.px, 0.px, 15.px)
			fontSize(1.4.cssRem)
			color(Color(ACCENT_COLOR))
		}
	}

	val content by style {
		padding(25.px, 15.px)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(30.px)
		maxWidth(1500.px)
		margin(0.px, autoLength)
	}

	val header by style {
		marginBottom(20.px)
		borderBottom(1.px, LineStyle.Solid, Color(BORDER_COLOR))
		paddingBottom(20.px)
	}

	val title by style {
		margin(0.px, 0.px, 10.px)
		fontSize(3.cssRem)

		"img" style {
			borderRadius(50.percent)
			marginRight(10.px)
			size(2.5.cssRem)
		}

		"span" style {
			fontSize(2.7.cssRem)
		}
	}

	val description by style {
		fontSize(1.4.cssRem)
		margin(0.px, 0.px, 20.px)
	}

	val quickStats by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(20.px)
		marginBottom(20.px)
	}

	val quickStatItem by style {
		color(Color(TEXT_SECONDARY))
		fontSize(1.1.cssRem)

		"i" style {
			color(Color(ACCENT_COLOR))
			marginRight(5.px)
		}
	}

	val headerTopics by style {
		marginTop(10.px)
		marginBottom(0.px)
	}

	val mainLayout by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("minmax(0, 2fr) minmax(300px, 1fr)")
		gap(30.px)

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				gridTemplateColumns("1fr")
			}
		}
	}

	val infoCard by style {
		background(Color(CARD_BACKGROUND))
		borderRadius(15.px)
		padding(20.px)
		marginBottom(20.px)
	}

	val statsGrid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(auto-fill, minmax(120px, 1fr))")
		gap(15.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val statCard by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(10.px)
		background(Color(ITEM_BACKGROUND))
		borderRadius(10.px)
		padding(15.px)
		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + hover style {
			background(Color(ITEM_BACKGROUND_HOVER))
			transform { scale(1.05) }
		}
	}

	val statIcon by style {
		color(Color(ACCENT_COLOR))
		fontSize(1.5.cssRem)
	}

	val statContent by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}

	val statValue by style {
		fontSize(1.2.cssRem)
		fontWeight("bold")
	}

	val statLabel by style {
		fontSize(0.9.cssRem)
		color(Color(TEXT_SECONDARY))
	}

	val linksList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(10.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val linkItem by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(10.px)
		background(Color(ITEM_BACKGROUND))
		borderRadius(10.px)
		padding(15.px)
		textDecoration("none")
		color(Color.white)
		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + hover style {
			background(Color(ITEM_BACKGROUND_HOVER))
			color(Color(LINK_HOVER_COLOR))
			transform { translateX(5.px) }
		}
	}

	val datesList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(10.px)
	}

	val dateItem by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(10.px)
		background(Color(ITEM_BACKGROUND))
		borderRadius(10.px)
		padding(15.px)
	}

	val dateLabel by style {
		fontWeight("bold")
	}

	val dateValue by style {
		marginLeft(autoLength)
		color(Color(ACCENT_COLOR))
	}

	val topicsContainer by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(15.px)
	}

	val topicsList by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(10.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val topicTag by style {
		background(Color("#B4BBFF20"))
		color(Color(ACCENT_COLOR))
		borderRadius(20.px)
		padding(5.px, 15.px)
		fontSize(0.9.cssRem)
		cursor(Cursor.Pointer)
		textDecoration("none")
		transitions {
			properties("all") {
				duration(0.3.s)
			}
		}

		self + hover style {
			background(Color("#B4BBFF40"))
			transform { scale(1.05) }
		}
	}

	val statusList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(10.px)
	}

	val statusItem by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(10.px)
		background(Color(ITEM_BACKGROUND))
		borderRadius(10.px)
		padding(15.px)
	}

	val statusLabel by style {
		fontWeight("bold")
	}

	val statusValue by style {
		marginLeft(autoLength)
		color(Color(ACCENT_COLOR))
	}

	val sidebarSection by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}

	val readmeSection by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(15.px)
	}

	val readme by style {
		background(Color(ITEM_BACKGROUND))
		borderRadius(20.px)
		margin(0.px)
		padding(30.px)

		"img" style {
			borderRadius(5.px)
			margin(10.px)
			maxWidth(100.percent)
		}

		"pre" {
			background(Color("#00000040"))
		}
	}

	val breadcrumbs by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		flexWrap(FlexWrap.Wrap)
		gap(8.px)
		marginBottom(25.px)
		padding(10.px, 15.px)
		backgroundColor(Color(ITEM_BACKGROUND))
		borderRadius(10.px)
		fontSize(0.9.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val breadcrumbLink by style {
		color(Color(TEXT_SECONDARY))
		textDecoration("none")
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(5.px)

		transitions {
			properties("color") {
				duration(0.2.s)
			}
		}

		self + hover style {
			color(Color(ACCENT_COLOR))
		}
	}

	val breadcrumbSeparator by style {
		color(Color(BORDER_COLOR))
		fontSize(0.7.cssRem)
	}

	val breadcrumbCurrent by style {
		color(Color(ACCENT_COLOR))
		fontWeight("bold")
	}
}
