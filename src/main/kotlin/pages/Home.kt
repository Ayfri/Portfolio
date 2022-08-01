package pages

import A
import androidx.compose.runtime.Composable
import localImage
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType.Enum.Screen
import org.jetbrains.compose.web.css.CSSMediaQuery.Only
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import style.AppStyle
import style.utils.ObjectFit
import style.utils.clamp
import style.utils.linearGradient
import style.utils.objectFit
import style.utils.size
import kotlin.js.Date

inline val years get() = (Date.now() - Date("2002-10-15").getTime()) / 1000 / 60 / 60 / 24 / 365

@Composable
fun Home() {
	Style(HomeStyle)
	
	Section({
		classes(HomeStyle.top)
	}) {
		Div({
			classes(HomeStyle.topInfo)
		}) {
			Img(localImage("avatar.jpg"), "avatar")
			
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
		
		Div {
			P({
				classes(AppStyle.monoFont)
			}) {
				Text("Hi, it’s me, Pierre Roy, I am a first year IT student at ")
				A("https://www.ynov.com/campus/aix-en-provence/", "Ynov Aix school")
				Text(
					"""
						, and I am passionate about computer science and especially programming.
						I’m making all sort of projects and programming by myself since years and this is my portfolio, welcome !
					""".trimIndent()
				)
			}
		}
	}
}

object HomeStyle : StyleSheet() {
	init {
		id("main") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Center)
			background(
				linearGradient(180.deg) {
					stop(Color("#1D1D1E"), (-3).percent)
					stop(Color("#111629"), 14.percent)
					stop(Color("#29183F"), 65.percent)
					stop(Color("#302F39"), 90.percent)
				}
			)
			
			paddingLeft(20.vw)
			paddingRight(20.vw)
			
			media(Only(MediaType(Screen), mediaMaxWidth(900.px))) {
				id("main") style {
					paddingLeft(10.vw)
					paddingRight(10.vw)
				}
			}
		}
	}
	
	@OptIn(ExperimentalComposeWebApi::class)
	val top by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		alignItems(AlignItems.Center)
		textAlign("center")
		
		"img" style {
			objectFit(ObjectFit.Cover)
			borderRadius(100.vmax)
			size(15.cssRem)
			filter {
				dropShadow(
					offsetX = 0.px,
					offsetY = 0.px,
					blurRadius = 30.px,
					color = rgba(0, 0, 0, .75)
				)
			}
		}
	}
	
	val topInfo by style {
		height(80.percent)
		width(clamp(34.cssRem, 23.vw, 38.cssRem))
		
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
}