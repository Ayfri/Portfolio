package io.github.ayfri.utils

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.SelectorsScope
import org.jetbrains.compose.web.css.StyleScope

//region Firefox
fun StyleScope.scrollbarColor(thumbColor: CSSColorValue, trackColor: CSSColorValue? = null) {
	property("scrollbar-color", "$thumbColor ${trackColor?.toString() ?: ""}")
}
//endregion

//region webkit
val SelectorsScope.webkitScrollbar get() = selector("::-webkit-scrollbar")
val SelectorsScope.webkitScrollbarThumb get() = selector("::-webkit-scrollbar-thumb")
val SelectorsScope.webkitScrollbarTrack get() = selector("::-webkit-scrollbar-track")
//endregion
