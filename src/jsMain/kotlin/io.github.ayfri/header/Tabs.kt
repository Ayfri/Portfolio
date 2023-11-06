package io.github.ayfri.header

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticMouseEvent
import app.softwork.routingcompose.NavLink
import org.jetbrains.compose.web.dom.Text

data class Tab(val name: String, val link: String)

val tabs = listOf(
	Tab("Home", "/"),
	Tab("About Me", "/about"),
	Tab("My Skills", "/skills"),
	Tab("My Projects", "/projects"),
	Tab("Experiences", "/experiences"),
	Tab("Portfolio", "/portfolio"),
)

@Composable
fun Tab(tab: Tab, onClick: (SyntheticMouseEvent) -> Unit = {}) {
	NavLink(tab.link, { selected ->
		if (selected) classes("active")

		onClick(onClick)
	}) {
		Text(tab.name)
	}
}
