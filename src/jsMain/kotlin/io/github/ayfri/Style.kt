package io.github.ayfri

import io.github.ayfri.header.HeaderStyle
import io.github.ayfri.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*

@OptIn(ExperimentalComposeWebApi::class)
object AppStyle : StyleSheet() {
	const val CONTENT_BACKGROUND_COLOR = "#212125"
	const val LINK_COLOR = "#75C9CE"
	const val LINK_HOVER_COLOR = "#95fbff"
	const val MONO_FONT_FAMILY = "JetBrains Mono"
	const val SPECIAL_TEXT_COLOR = "#B4BBFF"
	const val TITLE_BACKGROUND_COLOR = "#252525"
	const val TITLE_BORDER_COLOR = "#797979"

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
			property("scroll-behavior", "smooth")
			property("scroll-padding-top", HeaderStyle.navbarHeight.value())
		}

		"body" {
			fontFamily("Open Sans", "sans-serif")
			margin(0.px)

			scrollbarColor(scrollbarColor, scrollbarThumbColor)
			scrollbarWidth(scrollbarWidth)

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

			textDecoration("none")
		}

		desc("p", "a") style {
			color(Color(LINK_COLOR))

			hover(desc("p", "a")) style {
				color(Color(LINK_HOVER_COLOR))
			}
		}

		"button" {
			property("border", "none")

			transitions {
				defaultDelay(.25.s)
				properties("background-color", "color")
			}
		}

		id("main") style {
			backgroundColor(Color(CONTENT_BACKGROUND_COLOR))
			color(Color.white)
			marginTop(HeaderStyle.navbarHeight.value())
		}
	}

	val sections by style {
		margin(0.px, max(2.cssRem, 4.vw))
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
		backgroundColor(Color(TITLE_BACKGROUND_COLOR))
		border {
			color(Color(TITLE_BORDER_COLOR))
			style(LineStyle.Solid)
			width(2.px)
		}
		borderRadius(1.cssRem)
		fontSize(2.1.cssRem)

		margin(0.px, 0.px, 3.cssRem)
		padding(1.cssRem, 1.5.cssRem)

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
		fontSize(1.1.cssRem)
		fontWeight(700)
		padding(.7.cssRem, 1.2.cssRem)

		cursor(Cursor.Pointer)

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

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				size(max(7.cssRem, 30.vw))
			}
		}
	}
}
