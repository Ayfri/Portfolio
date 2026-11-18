package io.github.ayfri.utils

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.SelectorsScope
import org.jetbrains.compose.web.css.StyleScope

fun StyleScope.scrollbarColor(thumbColor: CSSColorValue, trackColor: CSSColorValue) {
	property("scrollbar-color", "$thumbColor $trackColor")
}

val SelectorsScope.webkitScrollbar get() = selector("::-webkit-scrollbar")
val SelectorsScope.webkitScrollbarThumb get() = selector("::-webkit-scrollbar-thumb")
val SelectorsScope.webkitScrollbarTrack get() = selector("::-webkit-scrollbar-track")
