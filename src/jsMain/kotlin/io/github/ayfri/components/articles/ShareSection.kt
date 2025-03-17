package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.AppStyle
import js.uri.encodeURIComponent
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import web.navigator.navigator

@Composable
fun ShareSection(title: String, url: String) {
	Style(ShareSectionStyle)

	Div({
		classes(ShareSectionStyle.container)
	}) {
		H3({
			classes(ShareSectionStyle.title)
		}) {
			I({
				classes("fas", "fa-share-alt")
			})
			Text(" Share this article")
		}

		Div({
			classes(ShareSectionStyle.buttons)
		}) {
			// X share (formerly Twitter)
			A(
				href = "https://twitter.com/intent/tweet?text=${encodeURIComponent(title)}&url=${encodeURIComponent(url)}",
				attrs = {
					attr("target", "_blank")
					attr("rel", "noopener noreferrer")
					classes(ShareSectionStyle.button, ShareSectionStyle.twitter)
				}
			) {
				I({
					classes("fab", "fa-x-twitter")
				})
				Span { Text("X") }
			}

			// LinkedIn share
			A(
				href = "https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(url)}",
				attrs = {
					attr("target", "_blank")
					attr("rel", "noopener noreferrer")
					classes(ShareSectionStyle.button, ShareSectionStyle.linkedin)
				}
			) {
				I({
					classes("fab", "fa-linkedin")
				})
				Span { Text("LinkedIn") }
			}

			// Facebook share
			A(
				href = "https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}",
				attrs = {
					attr("target", "_blank")
					attr("rel", "noopener noreferrer")
					classes(ShareSectionStyle.button, ShareSectionStyle.facebook)
				}
			) {
				I({
					classes("fab", "fa-facebook")
				})
				Span { Text("Facebook") }
			}

			val scope = rememberCoroutineScope()
			// Copy link button
			Button({
				classes(ShareSectionStyle.button, ShareSectionStyle.copyLink)
				onClick {
					scope.launch {
						navigator.clipboard.writeText(url)
					}
				}
			}) {
				I({
					classes("fas", "fa-link")
				})
				Span { Text("Copy Link") }
			}
		}
	}
}

object ShareSectionStyle : StyleSheet() {
	const val BORDER_TRANSPARENT_WHITE = "#FFFFFF20"
	const val SEMI_TRANSPARENT_WHITE = "#FFFFFFEE"
	const val X_BLACK = "#000000"
	const val X_BLACK_HOVER = "#333333"
	const val LINKEDIN_BLUE = "#0077B5"
	const val LINKEDIN_BLUE_HOVER = "#006699"
	const val FACEBOOK_BLUE = "#4267B2"
	const val FACEBOOK_BLUE_HOVER = "#365899"
	const val LIGHT_TRANSPARENT_WHITE = "#FFFFFF15"
	const val MEDIUM_TRANSPARENT_WHITE = "#FFFFFF25"

	val container by style {
		marginTop(4.cssRem)
		paddingTop(2.cssRem)
		borderTop(1.px, LineStyle.Companion.Solid, Color(BORDER_TRANSPARENT_WHITE))
		textAlign(TextAlign.Companion.Center)
	}

	val title by style {
		fontSize(1.3.cssRem)
		margin(0.px, 0.px, 1.5.cssRem)
		color(Color(SEMI_TRANSPARENT_WHITE))

		"i" {
			marginRight(0.5.cssRem)
		}
	}

	val buttons by style {
		display(DisplayStyle.Companion.Flex)
		flexWrap(FlexWrap.Companion.Wrap)
		justifyContent(JustifyContent.Companion.Center)
		gap(1.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val button by style {
		display(DisplayStyle.Companion.Flex)
		alignItems(AlignItems.Companion.Center)
		gap(0.5.cssRem)
		padding(0.6.cssRem, 1.2.cssRem)
		borderRadius(2.cssRem)
		border(0.px)
		fontSize(0.9.cssRem)
		fontWeight(600)
		cursor(Cursor.Companion.Pointer)
		textDecoration("none")

		transitions {
			"transform" {
				duration(0.2.s)
			}
			"opacity" {
				duration(0.2.s)
			}
		}

		self + hover style {
			transform {
				scale(1.05)
			}
		}

		self + active style {
			transform {
				scale(0.95)
			}
		}
	}

	val twitter by style {
		backgroundColor(Color(X_BLACK))
		color(Color.white)

		self + hover style {
			backgroundColor(Color(X_BLACK_HOVER))
		}
	}

	val linkedin by style {
		backgroundColor(Color(LINKEDIN_BLUE))
		color(Color.white)

		self + hover style {
			backgroundColor(Color(LINKEDIN_BLUE_HOVER))
		}
	}

	val facebook by style {
		backgroundColor(Color(FACEBOOK_BLUE))
		color(Color.white)

		self + hover style {
			backgroundColor(Color(FACEBOOK_BLUE_HOVER))
		}
	}

	val copyLink by style {
		backgroundColor(Color(LIGHT_TRANSPARENT_WHITE))
		color(Color.white)

		self + hover style {
			backgroundColor(Color(MEDIUM_TRANSPARENT_WHITE))
		}
	}

	// Responsive styles
	init {
		media(mediaMaxWidth(AppStyle.mobileThirdBreak)) {
			buttons style {
				flexDirection(FlexDirection.Companion.Column)
				alignItems(AlignItems.Companion.Stretch)
				gap(0.8.cssRem)
			}

			button style {
				justifyContent(JustifyContent.Companion.Center)
			}
		}
	}
}
