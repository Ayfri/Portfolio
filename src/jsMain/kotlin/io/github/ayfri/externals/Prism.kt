package io.github.ayfri.externals

import org.w3c.dom.ParentNode

external object Prism {
	fun highlightAll()
	fun highlightAllUnder(container: ParentNode)
}
