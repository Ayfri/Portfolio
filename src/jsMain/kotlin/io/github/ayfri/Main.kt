package io.github.ayfri

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.KobwebApp
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import io.github.ayfri.pages.NotFoundPage

const val MAIL_TO = "pierre.ayfri@gmail.com"

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) = KobwebApp { content() }

@InitKobweb
fun initKobweb(context: InitKobwebContext) {
	context.router.setErrorPage {
		NotFoundPage()
	}
}
