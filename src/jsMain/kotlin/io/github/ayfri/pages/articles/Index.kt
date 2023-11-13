package io.github.ayfri.pages.articles

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import io.github.ayfri.layouts.PageLayout

const val ARTICLES_PATH = "src/jsMain/resources/markdown/articles/"

@Page
@Composable
fun Index() {
	val fileList = MarkdownFiles.files

	PageLayout("Articles") {
	}
}
