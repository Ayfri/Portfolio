package style

import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.selectors.Nth
import style.CSSVariables.navbarHeight
import style.utils.*

object CSSVariables : StyleSheet() {
	val navbarHeight by variable<CSSNumeric>()
}


object AppStyle : StyleSheet() {
	const val monoFontFamily = "JetBrains Mono"
	const val navbarColor = "#2A2B36"
	const val navbarColorSelected = "#1e1c28"
	const val footerColor = "#1a1120"
	const val footerLinkHover = "#cccccc"
	const val contentBackgroundColor = "#212125"
	const val specialTextColor = "#B4BBFF"
	const val linkColor = "#75C9CE"
	const val linkHoverColor = "#95fbff"
	
	val mobileNavBarFirstBreak = 860.px
	
	init {
		root {
			CSSVariables.navbarHeight(5.cssRem)
		}
		
		"body" {
			fontFamily("Open Sans", "sans-serif")
			margin(0.px)
		}
		
		"a" style {
			transitions {
				delay(.25.s)
				properties("background-color", "color")
			}
			
			textDecoration("none")
		}
		
		desc("p", "a") style {
			color(Color(linkColor))
			
			hover(desc("p", "a")) style {
				color(Color(linkHoverColor))
			}
		}
		
		"button" {
			property("border", "none")
			
			transitions {
				delay(.25.s)
				properties("background-color", "color")
			}
		}
		
		id("main") style {
			backgroundColor(Color(contentBackgroundColor))
			color(Color.white)
			marginTop(navbarHeight.value())
			padding(1.cssRem)
		}
	}
	
	val monoFont by style {
		fontFamily(monoFontFamily, "monospace")
	}
	
	val numberColor by style {
		color(Color(specialTextColor))
	}
	
	@OptIn(ExperimentalComposeWebApi::class)
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
	}
	
	val navbar by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color(navbarColor))
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.SpaceBetween)
		position(Position.Fixed)
		size(navbarHeight.value(), 100.percent)
		top(0.px)
		zIndex(5)
		
		"i" style {
			fontSize(navbarHeight.value() * .6)
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
			height(100.percent)
			display(DisplayStyle.InlineBlock)
			lineHeight(navbarHeight.value())
			padding(0.px, clamp(1.5.cssRem, 3.vw, 2.8.cssRem))
			
			group(self + className("active"), hover(self)) style {
				backgroundColor(Color(navbarColorSelected))
			}
		}
	}
	
	val navbarGithub by style {
		borderRadius(1.5.cssRem)
		gap(1.cssRem)
		marginRight(2.cssRem)
		padding(.5.cssRem, 1.cssRem)
		
		hover(self) style {
			backgroundColor(Color(navbarColorSelected))
		}
		
		media(mediaMaxWidth(mobileNavBarFirstBreak)) {
			"p" {
				display(DisplayStyle.None)
			}
		}
	}
	
	val footer by style {
		background(linearGradient(180.deg) {
			stop(Color("#302F39"))
			stop(Color("#211C24"), 100.percent)
		})
		color(Color.white)
	}
	
	@OptIn(ExperimentalComposeWebApi::class)
	val footerContact by style {
		margin(0.px, auto)
		padding(1.cssRem, 0.px)
		position(Position.Relative)
		width(clamp(20.cssRem, 40.vw, 60.cssRem))
		
		"button" {
			backgroundColor(Color("#50435A"))
			borderRadius(.4.cssRem)
			color(Color.white)
			fontSize(1.1.cssRem)
			fontWeight(700)
			padding(.7.cssRem, 1.2.cssRem)
			
			position(Position.Relative)
			left(50.percent)
			transform {
				translateX((-50).percent)
			}
			
			cursor(Cursor.Pointer)
			
			filter {
				brightness(.8)
			}
			
			transitions {
				delay(.25.s)
				properties("filter")
			}
			
			"i" {
				paddingLeft(.4.cssRem)
			}
			
			hover(self) style {
				filter {
					brightness(1.0)
				}
			}
		}
	}
	
	val footerContactInputs by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns(repeat(2, 1.fr))
		gridTemplateRows(repeat(4, 1.fr))
		gap(1.cssRem)
		margin(1.cssRem, 0.px)
		
		child(self, nthChild(Nth.Functional(1))) style {
			gridArea("1", "1", "2", "2")
		}
		
		child(self, nthChild(Nth.Functional(2))) style {
			gridArea("1", "2", "2", "3")
		}
		
		child(self, nthChild(Nth.Functional(3))) style {
			gridArea("2", "1", "3", "3")
		}
		
		child(self, nthChild(Nth.Functional(4))) style {
			gridArea("3", "1", "5", "3")
		}
		
		child(self, universal) style {
			width(100.percent)
			height(100.percent)
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(.4.cssRem)
			
			"label" style {
				color(Color("#CFCFD2"))
				fontSize(1.cssRem)
			}
			
			list("input", "textarea") style {
				backgroundColor(Color("#252525"))
				borderRadius(.4.cssRem)
				color(Color("#FFFFFF"))
				border {
					color(Color.white)
					style(LineStyle.Solid)
					width(2.px)
				}
				fontFamily("Open Sans", "sans-serif")
				padding(.5.cssRem)
			}
			
			list("input::placeholder", "textarea::placeholder") style {
				color(Color("#FFFFFF7F"))
			}
			
			"textarea" style {
				height(100.percent)
				resize(Resize.None)
			}
		}
	}
	
	val footerInfo by style {
		backgroundColor(Color(footerColor))
		color(Color.white)
		padding(.5.cssRem, 0.px)
		textAlign("center")
		width(100.percent)
		
		className("top") style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			gap(2.cssRem)
			justifyContent(JustifyContent.Center)
			listStyle("none")
			padding(0.px)
			
			"a" style {
				color(Color.white)
				fontSize(2.5.cssRem)
				
				hover(self) style {
					color(Color(footerLinkHover))
				}
			}
		}
	}
}
