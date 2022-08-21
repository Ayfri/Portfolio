package pages

import A
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import data.DataStyle
import data.GitHubRepository
import data.HomeCard
import data.data
import localImage
import markdownParagraph
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType.Enum.Screen
import org.jetbrains.compose.web.css.CSSMediaQuery.Only
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import style.AppStyle
import style.utils.TextAlign
import style.utils.linearGradient
import style.utils.size
import style.utils.textAlign
import kotlin.js.Date

inline val years get() = (Date.now() - Date("2002-10-15").getTime()) / 1000 / 60 / 60 / 24 / 365

@Composable
fun Home() {
	val homeRepositories = remember { mutableStateListOf<GitHubRepository>() }
	
	Style(HomeStyle)
	Style(DataStyle)
	
	Section({
		classes(HomeStyle.top)
	}) {
		Div({
			classes(HomeStyle.topInfo)
		}) {
			Img(localImage("avatar.jpg"), "avatar") {
				classes(AppStyle.avatar)
			}
			
			H2 {
				Text("Pierre Roy ")
				
				Span {
					Text("alias")
				}
				
				Span {
					Text(" Ayfri")
				}
			}
			
			H3 {
				Text("Born ")
				
				Span({
					classes(AppStyle.monoFont, AppStyle.numberColor)
				}) {
					Text(years.toInt().toString())
				}
				
				Text(" years ago")
			}
			
			H3 {
				Text("IT Student")
			}
			
			H3 {
				Text("France")
			}
		}
		
		P({
			markdownParagraph(
				"""
					Hi, it's me, Pierre Roy, I am a first year IT student at [Ynov Aix school](https://www.ynov.com/campus/aix-en-provence/) and I am passionate about computer science and especially programming.
					I'm making all sort of projects and programming by myself since years and this is my portfolio, welcome!
				""".trimIndent(), true, AppStyle.monoFont
			)
		})
		
		if (homeRepositories.isEmpty()) {
			LaunchedEffect(Unit) {
				data.then { gitHubData ->
					homeRepositories += gitHubData.repos.sortedBy {
						it.stargazersCount
					}.reversed().take(3)
				}
			}
		}
		
		Section({
			classes(HomeStyle.section)
		}) {
			Div({
				classes("list", "repos")
			}) {
				homeRepositories.forEach {
					Div({
						classes(DataStyle.homeCard, "repo")
					}) {
						HomeCard(it)
					}
				}
			}
			
			A("/projects", "See all projects", AppStyle.button)
		}
		
		Section({
			classes(HomeStyle.section)
		}) {
			Div({
				classes("list", "skills")
			}) {
				skills.forEach {
					A("/skills#${it.language.name}", {
						classes("skill")
					}) {
						it.DisplaySimple()
					}
				}
			}
			
			A("/skills", "See all skills", AppStyle.button)
		}
	}
}

object HomeStyle : StyleSheet() {
	init {
		id("main") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			background(linearGradient(180.deg) {
				stop(Color("#1D1D1E"), (-3).percent)
				stop(Color("#111629"), 14.percent)
				stop(Color("#29183F"), 65.percent)
				stop(Color("#302F39"), 90.percent)
			})
			
			padding(1.cssRem, 8.5.vw)
		}
		
		media(Only(MediaType(Screen), mediaMaxWidth(AppStyle.mobileFirstBreak))) {
			id("main") style {
				paddingLeft(5.vw)
				paddingRight(5.vw)
			}
		}
		
		media(Only(MediaType(Screen), mediaMaxWidth(AppStyle.mobileFourthBreak))) {
			id("main") style {
				paddingLeft(2.vw)
				paddingRight(2.vw)
			}
		}
	}
	
	val top by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		textAlign(TextAlign.Center)
		maxWidth(100.percent)
	}
	
	val topInfo by style {
		height(80.percent)
		
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		
		universal style {
			marginTop(.2.cssRem)
			marginBottom(.2.cssRem)
		}
		
		"h2" style {
			fontSize(1.7.cssRem)
			
			universal style {
				fontSize(1.2.cssRem)
				fontWeight(400)
			}
			
			lastChild style {
				fontWeight(700)
				fontSize(1.4.cssRem)
			}
		}
		
		"h3" {
			fontWeight(400)
		}
	}
	
	@OptIn(ExperimentalComposeWebApi::class)
	val section by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		gap(1.5.cssRem)
		marginTop(3.cssRem)
		padding(0.px, 1.cssRem)
		
		className("skills") style {
			flexWrap(FlexWrap.Wrap)
		}
		
		className("list") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Row)
			justifyContent(JustifyContent.Center)
			gap(1.cssRem)
			
			className("skill") style {
				backgroundColor(Color("#252525"))
				borderRadius(.4.cssRem)
				color(Color.white)
				padding(.3.cssRem, .5.cssRem)
				
				"img" {
					borderRadius(.4.cssRem)
					size(3.5.cssRem)
				}
			}
		}
		
		media(Only(MediaType(Screen), mediaMaxWidth(AppStyle.mobileFirstBreak))) {
			className("repos") style {
				flexDirection(FlexDirection.Column)
				alignItems(AlignItems.Center)
				
				className("repo") style {
					maxWidth(95.percent)
				}
			}
		}
	}
}