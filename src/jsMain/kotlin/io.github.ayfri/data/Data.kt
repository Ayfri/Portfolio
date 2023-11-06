package io.github.ayfri.data

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.promise
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.js.Promise.Companion.resolve

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
	ignoreUnknownKeys = true
	namingStrategy = JsonNamingStrategy.SnakeCase
}

val ktorClient = HttpClient(JsClient())

const val DATA_URL = "https://raw.githubusercontent.com/Ayfri/Ayfri.github.io/api/result.json"

val data by lazy {
	val resolve = runCatching {
		localStorage["io.github.ayfri/dataithub.ayfri/data"]?.let {
			return@runCatching json.decodeFromString<GitHubData>(it)
		}
	}

	val resolved = resolve.getOrNull()

	return@lazy when {
		resolve.isFailure || resolved == null -> {
			CoroutineScope(window.asCoroutineDispatcher()).promise {
				val response = ktorClient.get(DATA_URL)
				val result = response.bodyAsText()
				localStorage["io.github.ayfri/dataithub.ayfri/data"] = result
				return@promise json.decodeFromString<GitHubData>(result)
			}
		}

		else -> resolved.let(::resolve)
	}
}
