package style

import org.jetbrains.compose.web.css.CSSBuilder
import org.jetbrains.compose.web.css.CSSNumeric
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.keywords.CSSAutoKeyword
import org.jetbrains.compose.web.css.width

fun CSSBuilder.size(height: CSSNumeric, width: CSSNumeric = height) {
	height(height)
	width(width)
}

fun CSSBuilder.size(height: CSSAutoKeyword, width: CSSAutoKeyword = height) {
	height(height)
	width(width)
}