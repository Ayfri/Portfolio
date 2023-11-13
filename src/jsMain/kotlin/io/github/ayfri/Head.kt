package io.github.ayfri

import kotlinx.browser.document

fun setTitle(title: String) {
	document.querySelector("title")?.textContent = title
}
