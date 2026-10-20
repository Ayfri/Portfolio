package io.github.ayfri.components.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.silk.components.icons.lucide.LucideStar
import io.github.ayfri.AppStyle
import io.github.ayfri.data.GOOGLE_SOURCE_LINK
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun PreferredSource() {
	P({
		style {
			alignItems(AlignItems.Center)
			color(Color(AppStyle.TEXT_MUTED_COLOR))
			display(DisplayStyle.Flex)
			flexWrap(FlexWrap.Wrap)
			fontSize(.95.cssRem)
			gap(.4.cssRem, .8.cssRem)
			marginTop(1.2.cssRem)
		}
	}) {
		Text("Want these articles first in Google Search and AI Mode?")

		A(GOOGLE_SOURCE_LINK, {
			classes(AppStyle.chip)
			target(ATarget.Blank)
			attr("rel", "noopener noreferrer")
			style {
				alignItems(AlignItems.Center)
				display(DisplayStyle.LegacyInlineFlex)
				gap(.4.cssRem)
			}
		}) {
			LucideStar()
			Text("Add ayfri.com as a preferred source on Google")
		}
	}
}
