package io.github.ayfri

import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*

@OptIn(ExperimentalComposeWebApi::class)
object AnimationsStyle : StyleSheet() {
	val appearFromBelow by keyframes {
		from {
			opacity(0)
			transform {
				translateY(10.cssRem)
			}
		}

		to {
			opacity(1)
			transform {
				translateY(0.px)
			}
		}
	}

	val appear by keyframes {
		from {
			opacity(0)
		}

		to {
			opacity(1)
		}
	}
}
