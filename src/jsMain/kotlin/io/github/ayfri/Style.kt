package io.github.ayfri

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.compose.css.functions.max
import io.github.ayfri.components.HeaderStyle
import io.github.ayfri.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.CSSSelector

fun StyleSheet.linkStyle(parent: CSSSelector) {
	desc(parent, "a") style {
		color(Color(AppStyle.LINK_COLOR))

		hover(desc(parent, "a")) style {
			color(Color(AppStyle.LINK_HOVER_COLOR))
		}
	}
}

fun StyleSheet.linkStyle(parent: String) = linkStyle(selector(parent))

@OptIn(ExperimentalComposeWebApi::class)
object AppStyle : StyleSheet() {
	const val CONTENT_BACKGROUND_COLOR = "#212125"
	const val LINK_COLOR = "#75C9CE"
	const val LINK_HOVER_COLOR = "#95fbff"
	const val MONO_FONT_FAMILY = "JetBrains Mono"
	const val SPECIAL_TEXT_COLOR = "#B4BBFF"

	val mobileFirstBreak = 890.px
	val mobileSecondBreak = 810.px
	val mobileThirdBreak = 510.px
	val mobileFourthBreak = 386.px

	val scrollbarColor = Color("#2D2D2D")
	val scrollbarThumbColor = Color("#555555")
	val scrollbarWidth = 13.px

	val buttonColor by variable<CSSColorValue>()

	init {
		"html" {
			scrollBehavior(ScrollBehavior.Smooth)
			scrollPaddingTop(HeaderStyle.navbarHeight.value())
		}

		"body" {
			backgroundColor(Color(CONTENT_BACKGROUND_COLOR))
			fontFamily("Open Sans", "sans-serif")
			fontOpticalSizing(FontOpticalSizing.Auto)
			margin(0.px)

			scrollbarColor(scrollbarColor, scrollbarThumbColor)
			scrollbarWidth(ScrollbarWidth.Thin)

			webkitScrollbar {
				width(scrollbarWidth)
			}

			webkitScrollbarThumb {
				backgroundColor(scrollbarThumbColor)
			}

			webkitScrollbarTrack {
				backgroundColor(scrollbarColor)
			}
		}

		"a" style {
			transitions {
				defaultDelay(.25.s)
				properties("background-color", "color")
			}

			textDecorationLine(TextDecorationLine.None)
		}

		linkStyle("p")

		"button" {
			borderStyle(LineStyle.None)

			transitions {
				defaultDelay(.25.s)
				properties("background-color", "color")
			}
		}

		id("main") style {
			color(Color.white)
			marginTop(HeaderStyle.navbarHeight.value())
		}
	}

	val sections by style {
		padding(3.cssRem)

		media(mediaMaxWidth(mobileFirstBreak)) {
			self {
				padding(1.5.cssRem)
				margin(0.px, .8.cssRem)
			}

			child(self, type("section")) style {
				gap(1.cssRem)
			}
		}

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				padding(.8.cssRem)
				margin(0.px)
			}
		}
	}

	val title by style {
		border(2.px, LineStyle.Solid, Color.transparent)
		background("""
			linear-gradient(#1A1225, #1A1225) padding-box,
			linear-gradient(45deg, #00D4FF, #FF0080) border-box
		""")
		borderRadius(1.cssRem)
		fontSize(2.1.cssRem)

		margin(0.px, 0.px, 3.cssRem)
		padding(1.cssRem, 1.5.cssRem)

		color(Color.white)

		media(mediaMaxWidth(mobileFirstBreak)) {
			self {
				fontSize(1.8.cssRem)
				padding(.8.cssRem, 1.4.cssRem)
			}
		}

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				fontSize(1.6.cssRem)
				marginBottom(1.8.cssRem)
			}
		}

		"span" {
			backgroundClip(BackgroundClip.Text)
			backgroundImage(linearGradient(45.deg) {
				add(Color("#00D4FF"))
				add(Color("#FF0080"))
			})
			property("-webkit-background-clip", "text")
			property("-webkit-text-fill-color", "transparent")
			property("-moz-text-fill-color", "transparent")
			property("-moz-background-clip", "text")
			property("text-shadow", "0 0 20px rgba(0, 212, 255, 0.5)")
		}
	}

	val monoFont by style {
		fontFamily(MONO_FONT_FAMILY, "monospace")
	}

	val numberColor by style {
		color(Color(SPECIAL_TEXT_COLOR))
	}

	val button by style {
		backgroundColor(buttonColor.value(Color("#252525")))
		borderRadius(.4.cssRem)
		color(Color.white)
		cursor(Cursor.Pointer)
		fontSize(1.1.cssRem)
		fontWeight(700)
		padding(.7.cssRem, 1.2.cssRem)

		filter {
			brightness(.8)
		}

		transitions {
			defaultDelay(.25.s)
			properties("filter")
		}

		group(hover(self), self + active) style {
			filter {
				brightness(1.0)
			}
		}
	}

	val avatar by style {
		borderRadius(100.vmax)
		objectFit(ObjectFit.Cover)
		size(15.cssRem)
		filter {
			dropShadow(
				offsetX = 0.px,
				offsetY = 0.px,
				blurRadius = 30.px,
				color = rgba(0, 0, 0, .75)
			)
		}

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				size(max(7.cssRem, 30.vw))
			}
		}
	}
}
