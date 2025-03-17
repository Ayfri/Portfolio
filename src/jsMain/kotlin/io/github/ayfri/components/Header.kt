package io.github.ayfri.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.css.whiteSpace
import io.github.ayfri.AppStyle.mobileFirstBreak
import io.github.ayfri.AppStyle.mobileSecondBreak
import io.github.ayfri.data.REPO_LINK
import io.github.ayfri.utils.*
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Header
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Nav

@Composable
fun Header() {
	Style(HeaderStyle)

	var open by mutableStateOf(false)

	Header({
		classes(HeaderStyle.navbar)
	}) {
		Nav({
			classes(HeaderStyle.navbarPart, HeaderStyle.navbarLinks)
			if (open) classes("open")
		}) {
			tabs.forEach {
				Tab(it) {
					open = !open
				}
			}
		}

		A(REPO_LINK, {
			target(ATarget.Blank)
			classes(HeaderStyle.navbarPart, HeaderStyle.navbarGithub)
			title("GitHub")
		}) {
			P("Ayfri")

			I(FontAwesomeType.BRAND, "github")
		}

		I({
			classes(HeaderStyle.mobileMenuButton, FontAwesomeType.SOLID.value, "fa-bars")

			onClick {
				open = !open
			}
		})
	}
}

object HeaderStyle : StyleSheet() {
	const val NAVBAR_COLOR = "#2A2B36"
	const val NAVBAR_COLOR_SELECTED = "#1e1c28"
	val navbarHeight by variable<CSSNumeric>()

	init {
		root {
			navbarHeight(4.5.cssRem)
		}
	}

	val navbar by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color(NAVBAR_COLOR))
		boxSizing("border-box")
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
		position(Position.Fixed)
		size(navbarHeight.value(), 100.percent)
		top(0.px)
		zIndex(5)

		"i" style {
			fontSize(navbarHeight.value() * .5)
		}

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				padding(0.px, 1.cssRem)
			}
		}
	}

	val navbarPart by style {
		alignItems(AlignItems.Center)
		color(Color.white)
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
	}

	val navbarLinks by style {
		"a" style {
			color(Color.white)
			display(DisplayStyle.InlineBlock)
			fontSize(0.95.cssRem)
			height(100.percent)
			lineHeight(navbarHeight.value())
			padding(0.px, clamp(1.3.cssRem, 2.5.vw, 2.3.cssRem))
			whiteSpace(WhiteSpace.NoWrap)

			group(self + className("active"), hover(self)) style {
				backgroundColor(Color(NAVBAR_COLOR_SELECTED))
			}
		}

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				display(DisplayStyle.None)
				position(Position.Absolute)
				top(navbarHeight.value().unsafeCast<CSSLengthOrPercentageValue>())
				left(0.px)
				width(100.percent)

				backgroundColor(Color(NAVBAR_COLOR))

				self + className("open") style {
					display(DisplayStyle.Flex)
					flexDirection(FlexDirection.Column)
					alignItems(AlignItems.Start)

					"a" {
						width(100.percent)
						lineHeight(navbarHeight.value() * .8)
					}
				}
			}
		}
	}

	val navbarGithub by style {
		borderRadius(.5.cssRem)
		gap(1.cssRem)
		height(80.percent)
		marginRight(1.vw)
		padding(.5.cssRem, 1.cssRem)

		hover(self) style {
			backgroundColor(Color(NAVBAR_COLOR_SELECTED))
		}

		media(mediaMaxWidth(mobileFirstBreak)) {
			self {
				padding(.5.cssRem)
			}

			"p" {
				display(DisplayStyle.None)
			}
		}
	}

	val mobileMenuButton by style {
		display(DisplayStyle.None)
		color(Color.white)
		cursor(Cursor.Pointer)

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				display(DisplayStyle.Block)
			}
		}
	}
}
