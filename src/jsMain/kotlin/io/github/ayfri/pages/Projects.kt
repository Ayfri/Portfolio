package io.github.ayfri.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.varabyte.kobweb.core.Page
import io.github.ayfri.AppStyle
import io.github.ayfri.data.DataStyle
import io.github.ayfri.data.GitHubRepository
import io.github.ayfri.data.ProjectCard
import io.github.ayfri.data.data
import io.github.ayfri.layouts.PageLayout
import io.github.ayfri.utils.minmax
import io.github.ayfri.utils.repeat
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

@Page
@Composable
fun Projects() {
	PageLayout("Projects") {
		Style(ProjectsStyle)
		Style(DataStyle)

		val repos = remember { mutableStateListOf<GitHubRepository>() }

		if (repos.isEmpty()) {
			data.then { gitHubData ->
				repos += gitHubData.repos.sortedWith(
					compareByDescending(GitHubRepository::stargazersCount).thenBy(String.CASE_INSENSITIVE_ORDER, GitHubRepository::fullName)
				)
			}
		}

		Div({
			classes(AppStyle.sections, ProjectsStyle.projects)
		}) {
			H1({
				classes(AppStyle.monoFont, AppStyle.title)
			}) {
				Text("My Projects:")
			}

			Section({
				classes(ProjectsStyle.projectsList)
			}) {
				repos.forEach {
					ProjectCard(it) onClick@{
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

object ProjectsStyle : StyleSheet() {
	const val PROJECTS_BACKGROUND_COLOR = "#15151C"

	val projects by style {
		backgroundColor(Color(PROJECTS_BACKGROUND_COLOR))
	}

	val projectsList by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns(repeat("auto-fill", minmax(22.5.cssRem, 1.fr)))
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
