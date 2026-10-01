package io.github.ayfri.pages.projects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.lucide.*
import io.github.ayfri.AppStyle
import io.github.ayfri.CodeTheme
import io.github.ayfri.components.GitHubIcon
import io.github.ayfri.components.Icon
import io.github.ayfri.data.GitHubRepository
import io.github.ayfri.data.portfolioData
import io.github.ayfri.externals.HighlightCode
import io.github.ayfri.jsonld.JsonLD
import io.github.ayfri.jsonld.generateProjectJsonLD
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.markdownParagraph
import io.github.ayfri.pages.IconHeading
import io.github.ayfri.pages.IconStyle
import io.github.ayfri.utils.list
import io.github.ayfri.utils.size
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import io.github.ayfri.components.Dd
import io.github.ayfri.components.Dl
import io.github.ayfri.components.Dt
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.dom.*
import web.location.location
import kotlin.js.Date

/** The URL of an HTML `src`/`href` attribute or of a markdown `[...](url)` link or image. */
private val readmeUrlRegex = Regex("""((?:src|href)="|\]\()([^")\s]+)""")
private val schemeRegex = Regex("""^[a-zA-Z][a-zA-Z0-9+.-]*:""")

/** README links and images are relative to the repository, so they are rewritten to its raw files. */
private fun GitHubRepository.absoluteReadme() = readmeContent?.replace(readmeUrlRegex) { match ->
	val (prefix, url) = match.destructured
	if (url.startsWith("#") || schemeRegex.containsMatchIn(url)) match.value
	else "$prefix$htmlUrl/raw/$defaultBranch/${url.removePrefix("./").removePrefix("/")}"
}

private fun formatDate(date: String) = Date(date).toLocaleDateString("en-GB")

@Page("{user}/{project}")
@Composable
fun All() {
	val context = rememberPageContext()
	val fullName = "${context.route.params["user"]}/${context.route.params["project"]}"
	val project = portfolioData().repos.find { it.fullName == fullName }

	if (project == null) {
		location.href = "/projects/"
		return
	}

	PageLayout(
		"${project.name} by ${project.owner.login}",
		description = project.description ?: AppGlobals["description"]!!,
		keywords = (project.topics.toList() + listOfNotNull(project.language)).distinct().joinToString(", "),
	) {
		Style(ProjectStyle)
		Style(IconStyle)
		Style(CodeTheme)
		HighlightCode()
		JsonLD(generateProjectJsonLD(project, context.route.toString()))

		val readme = remember(project) { project.absoluteReadme() }

		Div({
			classes(AppStyle.sections)
		}) {
			Nav({
				classes(ProjectStyle.breadcrumbs)
				attr("aria-label", "Breadcrumb")
			}) {
				A("/projects/") {
					LucideFolderOpen()
					Text(" Projects")
				}
				Text(" / ")
				A("/projects/?user=${project.owner.login}") { Text(project.owner.login) }
				Text(" / ")
				Span { Text(project.name) }
			}

			Header({
				classes(ProjectStyle.header)
			}) {
				H1({
					classes(AppStyle.title)
				}) {
					Img(project.owner.avatarUrl, project.owner.login)
					Text(project.name)
				}

				P({ classes(ProjectStyle.description) }) { Text(project.description ?: "No description provided.") }

				P({
					classes(AppStyle.monoFont, ProjectStyle.meta)
				}) {
					listOfNotNull<Pair<Icon, String>>(
						project.language?.let { Pair({ LucideCode() }, it) },
						Pair({ LucideStar() }, "${project.stargazersCount} stars"),
						Pair({ LucideGitBranch() }, "${project.forksCount} forks"),
						Pair({ LucideHistory() }, "updated ${formatDate(project.updatedAt)}"),
					).forEach { (icon, text) ->
						Span({ classes(IconStyle.label) }) {
							icon()
							Text(text)
						}
					}
				}

				if (project.topics.isNotEmpty()) Div({
					classes(ProjectStyle.topics)
				}) {
					project.topics.forEach { topic ->
						A("/projects/?tag=$topic", { classes(AppStyle.tag) }) { Text(topic) }
					}
				}
			}

			Div({
				classes(ProjectStyle.layout)
			}) {
				P({
					markdownParagraph(readme ?: "No README provided.", false, AppStyle.card, ProjectStyle.readme)
				})

				Aside({
					classes(ProjectStyle.sidebar)
				}) {
					Div({
						classes(AppStyle.card, ProjectStyle.panel)
					}) {
						IconHeading({ LucideLink() }, "Links")

						A(project.htmlUrl, {
							classes(AppStyle.button, AppStyle.primaryButton)
							target(ATarget.Blank)
						}) {
							GitHubIcon()
							Text("GitHub repository")
						}

						project.homepage?.takeIf { it.isNotBlank() }?.let {
							A(it, {
								classes(AppStyle.button)
								target(ATarget.Blank)
							}) {
								LucideGlobe()
								Text("Website")
							}
						}

						A("${project.htmlUrl}/archive/refs/heads/${project.defaultBranch}.zip", {
							classes(AppStyle.button)
						}) {
							LucideDownload()
							Text("Download ZIP")
						}
					}

					Div({
						classes(AppStyle.card, ProjectStyle.panel)
					}) {
						IconHeading({ LucideChartNoAxesColumn() }, "Details")

						Dl {
							listOfNotNull<Triple<Icon, String, String>>(
								Triple({ LucideStar() }, "Stars", project.stargazersCount.toString()),
								Triple({ LucideGitBranch() }, "Forks", project.forksCount.toString()),
								Triple({ LucideEye() }, "Watchers", project.watchersCount.toString()),
								Triple({ LucideCircleAlert() }, "Open issues", project.openIssuesCount.toString()),
								Triple({ LucideGitCommitHorizontal() }, "Commits", project.commitsCount.toString()),
								Triple({ LucideUsers() }, "Contributors", project.contributorsCount.toString()),
								Triple({ LucideHardDrive() }, "Size", "${project.size} KB"),
								Triple({ LucideCalendarPlus() }, "Created", formatDate(project.createdAt)),
								Triple({ LucideHistory() }, "Last push", formatDate(project.pushedAt)),
								Triple({ LucideGlobe() }, "Visibility", project.visibility.replaceFirstChar { it.titlecase() }),
								if (project.archived) Triple({ LucideArchive() }, "Archived", "Yes") else null,
								if (project.fork) Triple({ LucideGitFork() }, "Fork", "Yes") else null,
								if (project.isTemplate) Triple({ LucideCopy() }, "Template", "Yes") else null,
							).forEach { (icon, label, value) ->
								Dt {
									icon()
									Text(label)
								}
								Dd { Text(value) }
							}
						}
					}
				}
			}
		}
	}
}

object ProjectStyle : StyleSheet() {
	val breadcrumbs by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		fontSize(.9.cssRem)
		marginBottom(1.5.cssRem)

		"a" {
			color(Color(AppStyle.TEXT_MUTED_COLOR))
		}

		"a:hover" {
			color(Color.white)
		}

		"span" {
			color(Color.white)
		}
	}

	val header by style {
		borderBottom(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		marginBottom(2.cssRem)
		paddingBottom(1.8.cssRem)

		"h1" {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			gap(.8.cssRem)
			margin(0.px, 0.px, .8.cssRem)
		}

		"h1 img" {
			borderRadius(50.percent)
			size(2.6.cssRem)
		}
	}

	val description by style {
		fontSize(1.25.cssRem)
		margin(0.px, 0.px, .8.cssRem)
		maxWidth(48.cssRem)
	}

	val meta by style {
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(.85.cssRem)
		gap(.4.cssRem, 1.3.cssRem)
		margin(0.px, 0.px, 1.cssRem)
	}

	val topics by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(.4.cssRem)

		"a:hover" {
			color(Color.white)
		}
	}

	val layout by style {
		alignItems(AlignItems.Start)
		display(DisplayStyle.Grid)
		gap(1.5.cssRem)
		gridTemplateColumns("minmax(0, 2fr) minmax(17rem, 1fr)")

		media(mediaMaxWidth(AppStyle.mobileFirstBreak)) {
			self {
				gridTemplateColumns("minmax(0, 1fr)")
			}
		}
	}

	/** GitHub READMEs bring their own tables, code blocks and badges, scoped here so they never leak to other pages. */
	val readme by style {
		margin(0.px)
		overflowX(Overflow.Auto)
		padding(2.cssRem)

		"img" {
			maxWidth(100.percent)
		}

		"pre" {
			backgroundColor(Color(AppStyle.CONTENT_BACKGROUND_COLOR))
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
			borderRadius(.6.cssRem)
			overflowX(Overflow.Auto)
			padding(1.cssRem)
		}

		"table" {
			borderCollapse(BorderCollapse.Collapse)
		}

		list("td", "th") style {
			border(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
			padding(.5.cssRem, .75.cssRem)
		}

		list("h1", "h2", "h3") style {
			color(Color.white)
		}
	}

	val sidebar by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.5.cssRem)
		position(Position.Sticky)
		property("top", "calc(${io.github.ayfri.components.HeaderStyle.navbarHeight.value()} + 1.5rem)")
	}

	val panel by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(.6.cssRem)
		padding(1.4.cssRem)

		"h2" {
			color(Color.white)
			fontSize(1.cssRem)
			margin(0.px, 0.px, .4.cssRem)
		}

		"dl" {
			display(DisplayStyle.Grid)
			gap(.5.cssRem, 1.cssRem)
			gridTemplateColumns("auto 1fr")
			margin(0.px)
		}

		"dt" {
			alignItems(AlignItems.Center)
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			display(DisplayStyle.Flex)
			gap(.55.cssRem)
		}

		"dt svg" {
			color(Color(AppStyle.VIOLET_COLOR))
			fontSize(.8.em)
		}

		"dd" {
			fontFamily(AppStyle.MONO_FONT_FAMILY, "monospace")
			fontSize(.9.cssRem)
			margin(0.px)
			textAlign(TextAlign.Right)
		}
	}
}
