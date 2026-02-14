package io.github.ayfri

import com.varabyte.kobweb.compose.css.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.selectors.CSSSelector

object CodeTheme : StyleSheet() {
	val backgroundColor = Color.transparent
	val textColor = Color.white
	val classColor = Color("#ffcb6b")
	val commentColor = Color("#757575")
	val functionColor = Color("#82aaff")
	val keywordColor = Color("#c792ea")
	val punctuationColor = Color("#89ddff")
	val stringColor = Color("#c3e88d")
	val symbolColor = Color("#467cda")
	val valueColor = Color("#f78c6c")

	fun scope(vararg names: String): CSSSelector {
		val initial = className("token")
		val subClasses = names.map { initial + className(it) }
		return group(*subClasses.toTypedArray())
	}

	init {
		val toolbarSelector = child(type("div") + className("code-toolbar"), className("toolbar"))
		toolbarSelector style {
			display(DisplayStyle.Flex)
			gap(0.5.cssRem)
			alignItems(AlignItems.Center)

			fun buttonType(type: String) = child(toolbarSelector, child(className("toolbar-item"), type(type)))
			group(buttonType("button"), buttonType("a"), buttonType("span")) style {
				backgroundColor(Color("#333339"))
				cursor(Cursor.Default)
				padding(0.5.cssRem, 1.cssRem)
			}

			buttonType("button") style {
				cursor(Cursor.Pointer)
			}
		}

		desc(className("line-numbers"), className("line-numbers-rows")) style {
			left((-2.75).cssRem)
			width(1.75.cssRem)
		}

		child(type("pre") + attrContains("class", "language-"), type("code") + attrContains("class", "language-")) style {
			color(textColor)
			textShadow(TextShadow.of(color = Color.transparent, offsetX = 0.px, offsetY = 0.px, blurRadius = 0.px))
		}

		scope("comment") style {
			color(commentColor)
		}

		scope("number", "boolean") style {
			color(valueColor)
		}

		scope("string", "char", "regex") style {
			color(stringColor)
		}

		scope("keyword", "annotation.builtin") style {
			color(keywordColor)
		}

		scope("keyword", "boolean") style {
			fontStyle(FontStyle.Italic)
		}

		scope("property") style {
			color(keywordColor)
		}

		scope("symbol") style {
			color(symbolColor)
		}

		scope("punctuation", "operator") style {
			backgroundColor(backgroundColor)
			color(punctuationColor)
		}

		scope("interpolation-punctuation") style {
			color(classColor)
		}

		scope("function") style {
			color(functionColor)
		}

		scope("class-name", "builtin") style {
			color(classColor)
		}
	}
}
