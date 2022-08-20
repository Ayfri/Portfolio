package data

import A
import FontAwesomeType
import P
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.web.events.SyntheticMouseEvent
import markdownParagraph
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import pages.TextIcon
import style.AppStyle
import style.utils.*
import kotlin.js.Date

@Composable
fun HomeCard(repository: GitHubRepository) {
	H3 {
		Text(repository.name)
	}
	
	Div {
		P(repository.description ?: "No description provided.")
		
		P {
			Text("GitHub URL: ")
			
			A(repository.htmlUrl, repository.htmlUrl.substringAfter("github.com/"))
		}
	}
}

@Composable
fun ProjectCard(repository: GitHubRepository, onClick: AttrsScope<HTMLDivElement>.(SyntheticMouseEvent) -> Unit = {}) {
	var open by mutableStateOf(false)
	
	Div({
		classes(if (open) DataStyle.projectCardOpen else DataStyle.projectCardClosed, DataStyle.projectCard)
		
		onClick {
			open = !open
			onClick(it)
		}
	}) {
		Div({
			classes("top", AppStyle.monoFont)
		}) {
			if (open) {
				Img(src = repository.owner.avatarUrl)
				H2 { Text(repository.name) }
				
				Div({
					classes("texts")
				}) {
					P({
						val creationDate = Date(repository.createdAt)
						val day = creationDate.getDate().toString().padStart(2, '0')
						val month = (creationDate.getMonth() + 1).toString().padStart(2, '0')
						val year = creationDate.getFullYear()
						val formatted = "$day/$month/$year"
						
						markdownParagraph(
							"""
							Creation date: <span>$formatted</span>
							Stars: <span>${repository.stargazersCount}</span>
							Commits: <span>${repository.commitsCount}</span>
							Contributors: <span>${repository.contributorsCount}</span>
						""".trimIndent(), true
						)
					})
				}
			} else {
				Img(src = repository.owner.avatarUrl)
				Div({
					classes("stars")
				}) {
					TextIcon(repository.stargazersCount.toString(), FontAwesomeType.SOLID, "star")
				}
			}
		}
		
		Div({
			classes("bottom")
		}) {
			if (open) {
				val readmeContent = repository.readmeContent ?: repository.description ?: repository.name
				
				P({
					markdownParagraph(readmeContent)
				})
			} else {
				H3 { Text(repository.name) }
				P(repository.description ?: "No description provided.")
			}
		}
	}
}

object DataStyle : StyleSheet() {
	const val homeCardBackground = "#181818"
	const val homeCardTitleBackground = "#0e0e0e"
	const val homeCardColor = "#cacaca"
	const val projectCardClosedBackground = "#2a2b36"
	const val projectCardOpenBackgroundStart = "#66085190"
	const val projectCardOpenBackgroundEnd = "#39379490"
	
	val gridColumnStartVar by variable<StylePropertyNumber>()
	val imageSize by variable<CSSSizeValue<*>>()
	
	@OptIn(ExperimentalComposeWebApi::class)
	val homeCard by style {
		borderRadius(.8.cssRem)
		overflow(Overflow.Hidden)
		width(25.cssRem)
		
		transitions {
			ease(AnimationTimingFunction.EaseInOut)
			duration(.2.s)
			
			properties("transform")
		}
		
		transform {
			scale(1.0)
		}
		
		"h3" {
			backgroundColor(Color(homeCardTitleBackground))
			color(Color.white)
			margin(0.px)
			padding(1.cssRem)
		}
		
		"p" {
			color(Color(homeCardColor))
		}
		
		child(self, selector("div")) style {
			backgroundColor(Color(homeCardBackground))
			textAlign(TextAlign.Start)
			
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(3.cssRem)
			
			padding(1.cssRem)
		}
		
		combine(self, className("active")) style {
			transform {
				scale(1.05)
			}
		}
	}
	
	val projectCard by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(1.cssRem)
		padding(clamp(2.cssRem, 2.vw, 3.5.cssRem))
		borderRadius(.75.cssRem)
		
		cursor(Cursor.Pointer)
		
		transitions {
			ease(AnimationTimingFunction.EaseInOut)
			duration(.5.s)
			properties("max-height")
		}
		
		"img" {
			size(imageSize.value())
			borderRadius(imageSize.value())
		}
	}
	
	val projectCardOpen by style {
		flexDirection(FlexDirection.Column)
		gap(2.cssRem)
		imageSize(5.cssRem)
		gridColumn("${gridColumnStartVar.value()} span")
		
		backgroundImage(linearGradient(135.deg) {
			stop(Color(projectCardOpenBackgroundStart))
			stop(Color(projectCardOpenBackgroundEnd))
		})
		maxHeight(500.cssRem)
		
		className("top") style {
			textAlign(TextAlign.Center)
			
			"h2" {
				fontSize(2.5.cssRem)
			}
			
			"p" {
				textAlign(TextAlign.Start)
				margin(0.px, 1.5.cssRem)
			}
			
			inline fun subSpanColor(color: CSSColorValue, index: Int) {
				child(type("p"), type("span")) + nthOfType(index.n) style {
					color(color)
				}
			}
			
			subSpanColor(Color("#B4BBFF"), 1)
			subSpanColor(Color("#FFE24B"), 2)
			subSpanColor(Color("#75C9CE"), 3)
			subSpanColor(Color("#64E881"), 4)
		}
		
		className("bottom") style {
			backgroundColor(Color("#ffffff20"))
			borderRadius(.75.cssRem)
			padding(clamp(2.cssRem, 2.vw, 3.5.cssRem))
			maxWidth(90.percent)
			
			"h1" {
				marginTop(0.px)
			}
			
			"code" {
				whiteSpace("pre-wrap")
			}
		}
	}
	
	val projectCardClosed by style {
		flexDirection(FlexDirection.Row)
		imageSize(3.5.cssRem)
		
		backgroundColor(Color(projectCardClosedBackground))
		height(6.cssRem)
		maxHeight(6.cssRem)
		
		className("top") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			textAlign(TextAlign.Center)
			gap(.5.cssRem)
			
			className("stars") style {
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.RowReverse)
				alignItems(AlignItems.Center)
				justifyContent(JustifyContent.Center)
				gap(.5.cssRem)
			}
		}
		
		className("bottom") style {
			group(type("h3"), type("p")) style {
				property("text-ellipsis", "ellipsis")
				margin(0.px)
			}
			
			"h3" {
				marginBottom(.5.cssRem)
			}
		}
	}
}