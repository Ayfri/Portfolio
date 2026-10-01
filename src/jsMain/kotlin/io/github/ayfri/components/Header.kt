package io.github.ayfri.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.navigation.Anchor
import com.varabyte.kobweb.silk.components.icons.lucide.LucideMenu
import io.github.ayfri.AppStyle
import io.github.ayfri.AppStyle.mobileSecondBreak
import io.github.ayfri.data.REPO_LINK
import io.github.ayfri.localImage
import io.github.ayfri.tint
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

private const val NAV_ID = "site-nav"

/**
 * On mobile the nav is a native popover opened by the menu button, which brings Escape and click-outside closing
 * for free. On desktop the popover styles are reset so it renders as a plain inline nav.
 */
@Composable
fun Header() {
	Style(HeaderStyle)

	Header({
		classes(HeaderStyle.navbar)
	}) {
		Anchor("/", {
			classes(HeaderStyle.brand)
		}) {
			Img(localImage("logo-64.webp"), "") {
				attr("width", "61")
				attr("height", "64")
			}
			Text("Pierre Roy")
		}

		Nav({
			classes(HeaderStyle.navbarLinks)
			id(NAV_ID)
			attr("popover", "auto")
		}) {
			tabs.forEach {
				Tab(it) { document.getElementById(NAV_ID).asDynamic().hidePopover() }
			}
		}

		A(REPO_LINK, {
			target(ATarget.Blank)
			classes(HeaderStyle.iconButton)
			title("GitHub")
			attr("aria-label", "GitHub")
		}) {
			GitHubIcon()
		}

		Button({
			classes(HeaderStyle.iconButton, HeaderStyle.mobileMenuButton)
			attr("aria-label", "Menu")
			attr("popovertarget", NAV_ID)
		}) {
			LucideMenu()
		}
	}
}

object HeaderStyle : StyleSheet() {
	val navbarHeight by variable<CSSLengthValue>()

	init {
		root {
			navbarHeight(4.cssRem)
		}
	}

	/** Named so page transitions leave the header in place instead of fading it with the page. */
	val navbar by style {
		property("view-transition-name", "site-header")
		alignItems(AlignItems.Center)
		backdropFilter(BackdropFilter.list(BackdropFilter.of(blur(12.px))))
		backgroundColor(Color(tint(AppStyle.CONTENT_BACKGROUND_COLOR, 80)))
		borderBottom(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		gap(.5.cssRem)
		padding(0.px, clamp(1.cssRem, 4.vw, 3.cssRem))
		position(Position.Fixed)
		height(navbarHeight.value())
		width(100.percent)
		top(0.px)
		zIndex(100)
	}

	val brand by style {
		alignItems(AlignItems.Center)
		color(Color.white)
		display(DisplayStyle.Flex)
		fontFamily(AppStyle.MONO_FONT_FAMILY, "monospace")
		fontWeight(700)
		gap(.65.cssRem)
		marginRight(autoLength)
		whiteSpace(WhiteSpace.NoWrap)

		"img" {
			height(1.7.cssRem)
			width(Width.FitContent)
		}
	}

	val navbarLinks by style {
		// Resets the user agent popover box (fixed, centered, bordered), the nav only behaves as a popover on mobile.
		backgroundColor(Color.transparent)
		border(0.px)
		color(CSSColor.Inherit)
		display(DisplayStyle.Flex)
		height(100.percent)
		margin(0.px)
		overflow(Overflow.Visible)
		padding(0.px)
		position(Position.Static)

		"a" style {
			alignItems(AlignItems.Center)
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			display(DisplayStyle.Flex)
			fontSize(.95.cssRem)
			fontWeight(500)
			padding(0.px, clamp(.6.cssRem, 1.3.vw, 1.1.cssRem))
			position(Position.Relative)
			whiteSpace(WhiteSpace.NoWrap)

			hover(self) style {
				color(Color.white)
			}

			/** The active tab and its underline slide to the new tab during page transitions. */
			self + className("active") style {
				color(Color.white)
				property("view-transition-name", "nav-active")
			}
		}

		"a.active::after" {
			content("")
			backgroundColor(Color(AppStyle.ACCENT_COLOR))
			bottom((-1).px)
			height(2.px)
			position(Position.Absolute)
			property("inset-inline", "clamp(.6rem, 1.3vw, 1.1rem)")
		}

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				display(DisplayStyle.None)
			}

			self + selector(":popover-open") style {
				backgroundColor(Color(AppStyle.CONTENT_BACKGROUND_COLOR))
				borderBottom(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
				display(DisplayStyle.Flex)
				flexDirection(FlexDirection.Column)
				height(Height.FitContent)
				padding(.5.cssRem, 0.px)
				position(Position.Fixed)
				property("inset", "${navbarHeight.value()} 0 auto 0")
				width(100.percent)
			}

			"a" style {
				fontSize(1.05.cssRem)
				padding(.8.cssRem, 1.5.cssRem)
			}

			"a.active::after" {
				property("inset", "0 auto 0 0")
				width(2.px)
			}
		}
	}

	val iconButton by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		borderRadius(.5.cssRem)
		color(Color(AppStyle.TEXT_MUTED_COLOR))
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(1.3.cssRem)
		height(2.4.cssRem)
		justifyContent(JustifyContent.Center)
		width(2.4.cssRem)

		hover(self) style {
			backgroundColor(Color("#FFFFFF0F"))
			color(Color.white)
		}
	}

	val mobileMenuButton by style {
		display(DisplayStyle.None)

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				display(DisplayStyle.Flex)
			}
		}
	}
}
