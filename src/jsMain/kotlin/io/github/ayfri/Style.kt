package io.github.ayfri

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.max
import io.github.ayfri.components.HeaderStyle
import io.github.ayfri.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
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

/** Translucent version of a palette color, `tint(ACCENT_COLOR, 25)` is the accent at 25% opacity. */
fun tint(color: String, percent: Int) = "color-mix(in oklab, $color $percent%, transparent)"

/**
 * Palette taken from the logo, a neon tube fading cyan -> violet -> orchid: violet-black surfaces, cyan for actions,
 * violet for ambient tints, and the full logo gradient only on the few elements that should glow.
 */
object AppStyle : StyleSheet() {
	const val CONTENT_BACKGROUND_COLOR = "#0B0916"
	/** Solid surface for cards and panels, one step above the page background. */
	const val CARD_BACKGROUND = "#13101F"
	const val SURFACE_RAISED = "#1C1830"
	const val BORDER_COLOR = "#D9CCFF1C"
	const val BORDER_STRONG_COLOR = "#D9CCFF38"

	const val TEXT_COLOR = "#ECE9F5"
	/** 7.5:1 on the page background, the floor for body copy. */
	const val TEXT_MUTED_COLOR = "#A7A0BD"

	/** Logo cyan: links, active states, focus rings and the primary button. */
	const val ACCENT_COLOR = "#3EE6F0"
	/** Logo violet: ambient glows, selected chips, hover tints. */
	const val VIOLET_COLOR = "#A67CF5"
	/** Logo orchid, the end of the brand gradient. */
	const val ACCENT_SECONDARY_COLOR = "#EE74F2"

	const val LINK_COLOR = "#6FEBF3"
	const val LINK_HOVER_COLOR = "#C6F8FB"
	const val SPECIAL_TEXT_COLOR = ACCENT_COLOR

	const val BRAND_GRADIENT = "linear-gradient(135deg, $ACCENT_COLOR, $VIOLET_COLOR 55%, $ACCENT_SECONDARY_COLOR)"

	const val MONO_FONT_FAMILY = "JetBrains Mono"
	const val SANS_FONT_FAMILY = "Source Sans 3"

	val mobileFirstBreak = 890.px
	val mobileSecondBreak = 810.px
	val mobileThirdBreak = 510.px
	val mobileFourthBreak = 386.px

	val scrollbarColor = Color(CONTENT_BACKGROUND_COLOR)
	val scrollbarThumbColor = Color("#2E2745")
	val scrollbarWidth = 13.px

	val buttonColor by variable<CSSColorValue>()

	/** `--reveal-shift` drops to 0 under reduced motion, leaving a plain fade. */
	val revealUp by keyframes {
		from {
			opacity(0)
			property("translate", "0 var(--reveal-shift, 1.5rem)")
		}
		to {
			opacity(1)
			property("translate", "0 0")
		}
	}

	val pageOut by keyframes {
		to {
			opacity(0)
			transform { translateY((-.5).cssRem) }
		}
	}

	val pageIn by keyframes {
		from {
			opacity(0)
			transform { translateY(1.cssRem) }
		}
	}

	init {
		/** Page swaps started by [enableViewTransitions]: the old page lifts away, the new one rises in. */
		"::view-transition-old(root)" {
			property("animation", "${pageOut.name} .2s ease-in both")
		}

		"::view-transition-new(root)" {
			property("animation", "${pageIn.name} .4s cubic-bezier(.2, .8, .2, 1) .05s both")
		}

		"::view-transition-group(page-title)" {
			property("animation-duration", ".45s")
			property("animation-timing-function", "cubic-bezier(.2, .8, .2, 1)")
		}

		"::view-transition-group(nav-active)" {
			property("animation-duration", ".35s")
			property("animation-timing-function", "cubic-bezier(.3, 1.4, .5, 1)")
		}

		media(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("reduce"))) {
			"::view-transition-group(*), ::view-transition-old(*), ::view-transition-new(*)" {
				property("animation", "none")
			}

			":root" {
				property("--reveal-shift", "0")
			}
		}

		"html" {
			property("accent-color", ACCENT_COLOR)
			property("color-scheme", "dark")
			property("interpolate-size", "allow-keywords")
			property("scrollbar-gutter", "stable")
			scrollBehavior(ScrollBehavior.Smooth)
			scrollPaddingTop(HeaderStyle.navbarHeight.value())
		}

		/**
		 * Starfield tile under a violet nebula and a smaller cyan one, pinned to the top of the document and painted
		 * once on `body`, so pages never need their own background.
		 */
		"body" {
			backgroundColor(Color(CONTENT_BACKGROUND_COLOR))
			backgroundImage(
				"radial-gradient(ellipse 75% 40rem at 15% -8rem, ${tint(VIOLET_COLOR, 22)}, transparent 70%), " +
					"radial-gradient(ellipse 50% 28rem at 90% 2rem, ${tint(ACCENT_COLOR, 9)}, transparent 70%), " +
					"url('/images/stars.svg')"
			)
			backgroundRepeat("no-repeat, no-repeat, repeat")
			color(Color(TEXT_COLOR))
			fontFamily(SANS_FONT_FAMILY, "system-ui", "sans-serif")
			fontOpticalSizing(FontOpticalSizing.Auto)
			fontSize(1.0625.cssRem)
			lineHeight(1.6.number)
			margin(0.px)
			property("-webkit-font-smoothing", "antialiased")

			scrollbarColor(scrollbarThumbColor, scrollbarColor)
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

		"h1, h2, h3" {
			fontFamily(MONO_FONT_FAMILY, "monospace")
			letterSpacing((-0.02).em)
			lineHeight(1.2.number)
			property("text-wrap", "balance")
		}

		"p, li" {
			property("text-wrap", "pretty")
		}

		/**
		 * Inline SVGs are the [io.github.ayfri.components.Icon]s, their ink stops 1px short of their 24px grid so 1.15em matches a glyph.
		 * An SVG has no text baseline, so icon rows align their items on `baseline` (the text's) and the icon centers itself.
		 */
		"svg" {
			alignSelf(AlignSelf.Center)
			flexShrink(0)
			height(1.15.em)
			property("vertical-align", "-.25em")
			width(1.15.em)
		}

		"::selection" {
			backgroundColor(Color(tint(VIOLET_COLOR, 45)))
			color(Color.white)
		}

		":focus-visible" {
			property("outline", "2px solid $ACCENT_COLOR")
			property("outline-offset", "2px")
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
			fontFamily("inherit")

			transitions {
				defaultDelay(.25.s)
				properties("background-color", "color")
			}
		}

		id("main") style {
			color(Color(TEXT_COLOR))
			marginTop(HeaderStyle.navbarHeight.value())
		}
	}

	val sections by style {
		boxSizing(BoxSizing.BorderBox)
		margin(0.px, autoLength)
		maxWidth(1200.px + 6.cssRem)
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

	/** Page-level `h1`: plain and large, the page's first line of text rather than a boxed badge. Morphs across pages. */
	val title by style {
		property("view-transition-name", "page-title")
		property("width", "fit-content")
		color(Color.white)
		fontFamily(MONO_FONT_FAMILY, "monospace")
		fontSize(2.6.cssRem)
		fontWeight(700)
		margin(1.cssRem, 0.px, 2.5.cssRem)

		media(mediaMaxWidth(mobileFirstBreak)) {
			self {
				fontSize(2.1.cssRem)
			}
		}

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				fontSize(1.8.cssRem)
				marginBottom(1.8.cssRem)
			}
		}
	}

	val card by style {
		backgroundColor(Color(CARD_BACKGROUND))
		border(1.px, LineStyle.Solid, Color(BORDER_COLOR))
		borderRadius(.9.cssRem)
		boxSizing(BoxSizing.BorderBox)
		revealOnScroll()
	}

	val tag by style {
		backgroundColor(Color("#FFFFFF0D"))
		borderRadius(.4.cssRem)
		color(Color(TEXT_MUTED_COLOR))
		fontFamily(MONO_FONT_FAMILY, "monospace")
		fontSize(.78.cssRem)
		padding(.2.cssRem, .6.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
	}

	/** Toggleable filter pill, `active` class for the selected state. */
	val chip by style {
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, Color(BORDER_STRONG_COLOR))
		borderRadius(2.cssRem)
		color(Color(TEXT_MUTED_COLOR))
		cursor(Cursor.Pointer)
		fontSize(.85.cssRem)
		padding(.3.cssRem, .85.cssRem)

		hover(self) style {
			borderColor(Color(tint(VIOLET_COLOR, 70)))
			color(Color.white)
		}

		group(self + className("active"), self + className("active") + hover) style {
			backgroundColor(Color(tint(VIOLET_COLOR, 22)))
			borderColor(Color(VIOLET_COLOR))
			color(Color.white)
		}
	}

	val input by style {
		backgroundColor(Color(CARD_BACKGROUND))
		border(1.px, LineStyle.Solid, Color(BORDER_STRONG_COLOR))
		borderRadius(.5.cssRem)
		boxSizing(BoxSizing.BorderBox)
		color(Color.white)
		fontFamily("inherit")
		fontSize(1.cssRem)
		padding(.6.cssRem, .8.cssRem)

		self + focus style {
			borderColor(Color(ACCENT_COLOR))
			boxShadow("0 0 0 3px ${tint(ACCENT_COLOR, 18)}")
			outlineStyle(LineStyle.None)
		}

		"option" {
			backgroundColor(Color(CARD_BACKGROUND))
		}
	}

	val monoFont by style {
		fontFamily(MONO_FONT_FAMILY, "monospace")
	}

	val numberColor by style {
		color(Color(SPECIAL_TEXT_COLOR))
	}

	/** Secondary action: outlined, the accent only shows up on hover. [buttonColor] overrides the fill. */
	val button by style {
		alignItems(AlignItems.Baseline)
		backgroundColor(buttonColor.value(Color(SURFACE_RAISED)))
		border(1.px, LineStyle.Solid, Color(BORDER_STRONG_COLOR))
		borderRadius(.5.cssRem)
		color(Color(TEXT_COLOR))
		cursor(Cursor.Pointer)
		display(DisplayStyle.LegacyInlineFlex)
		fontSize(1.cssRem)
		fontWeight(600)
		gap(.5.cssRem)
		padding(.6.cssRem, 1.1.cssRem)

		transitions {
			properties("border-color", "color", "background-color") {
				duration(.2.s)
			}
		}

		group(hover(self), self + active) style {
			borderColor(Color(ACCENT_COLOR))
			color(Color.white)
		}
	}

	/** Primary action: the only filled neon element of a view, so keep one per section at most. */
	val primaryButton by style {
		backgroundColor(Color(ACCENT_COLOR))
		borderColor(Color(ACCENT_COLOR))
		color(Color(CONTENT_BACKGROUND_COLOR))

		group(hover(self), self + active) style {
			backgroundColor(Color(LINK_HOVER_COLOR))
			borderColor(Color(LINK_HOVER_COLOR))
			color(Color(CONTENT_BACKGROUND_COLOR))
			boxShadow("0 0 24px ${tint(ACCENT_COLOR, 35)}")
		}
	}

	val avatar by style {
		borderRadius(100.vmax)
		objectFit(ObjectFit.Cover)
		size(15.cssRem)

		media(mediaMaxWidth(mobileSecondBreak)) {
			self {
				size(max(7.cssRem, 30.vw))
			}
		}
	}
}
