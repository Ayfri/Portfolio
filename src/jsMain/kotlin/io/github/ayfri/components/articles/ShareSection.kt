package io.github.ayfri.components.articles

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.borderTop
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCheck
import com.varabyte.kobweb.silk.components.icons.lucide.LucideLink
import io.github.ayfri.AppStyle
import io.github.ayfri.components.BlueskyIcon
import io.github.ayfri.components.Icon
import io.github.ayfri.components.LinkedInIcon
import io.github.ayfri.components.XIcon
import js.uri.encodeURIComponent
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import web.navigator.navigator

private data class ShareTarget(val name: String, val icon: Icon, val link: (title: String, url: String) -> String)

private val shareTargets = listOf(
	ShareTarget("X", { XIcon() }) { title, url -> "https://twitter.com/intent/tweet?text=$title&url=$url" },
	ShareTarget("LinkedIn", { LinkedInIcon() }) { _, url -> "https://www.linkedin.com/sharing/share-offsite/?url=$url" },
	ShareTarget("Bluesky", { BlueskyIcon() }) { title, url -> "https://bsky.app/intent/compose?text=$title%20$url" },
)

@Composable
fun ShareSection(title: String, url: String) {
	var copied by remember { mutableStateOf(false) }

	Section({
		style {
			alignItems(AlignItems.Center)
			borderTop(1.px, LineStyle.Solid, Color(AppStyle.BORDER_COLOR))
			display(DisplayStyle.Flex)
			flexWrap(FlexWrap.Wrap)
			gap(.6.cssRem)
			marginTop(3.cssRem)
			paddingTop(1.5.cssRem)
		}
	}) {
		Span({
			style {
				color(Color(AppStyle.TEXT_MUTED_COLOR))
				marginRight(.4.cssRem)
			}
		}) {
			Text("Share this article")
		}

		shareTargets.forEach { target ->
			A(target.link(encodeURIComponent(title), encodeURIComponent(url)), {
				classes(AppStyle.button)
				target(ATarget.Blank)
				attr("rel", "noopener noreferrer")
			}) {
				target.icon()
				Text(target.name)
			}
		}

		Button({
			classes(AppStyle.button)
			onClick {
				navigator.clipboard.writeTextAsync(url)
				copied = true
			}
		}) {
			if (copied) LucideCheck() else LucideLink()
			Text(if (copied) "Copied" else "Copy link")
		}
	}
}
