package io.github.ayfri.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import kotlinx.browser.window
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal const val GITHUB_PORTFOLIO_JSON_URL = "/data/github-portfolio.json"

private val loadScope = MainScope()
private val deferredGate = Mutex()

private var portfolioCache: GitHubData? = null
private var loadDeferred: Deferred<GitHubData>? = null

fun prefetchPortfolioData() {
	if (portfolioCache != null) return
	loadScope.launch {
		runCatching { loadPortfolioData() }
	}
}

private suspend fun fetchAndParse(): GitHubData {
	val response = window.fetch(GITHUB_PORTFOLIO_JSON_URL).await()
	if (!response.ok) {
		throw IllegalStateException("Failed to load $GITHUB_PORTFOLIO_JSON_URL (HTTP ${response.status})")
	}
	val text = response.text().await()
	return JSON.parse<GitHubData>(text, ::snakeCaseReviver)
}

private suspend fun deferredForLoad(): Deferred<GitHubData> =
	deferredGate.withLock {
		portfolioCache?.let {
			return@withLock loadScope.async { it }
		}
		loadDeferred?.let { return@withLock it }
		loadScope.async { fetchAndParse() }.also { loadDeferred = it }
	}

suspend fun loadPortfolioData(): GitHubData {
	portfolioCache?.let { return it }
	val result = deferredForLoad().await()
	deferredGate.withLock {
		if (portfolioCache == null) {
			portfolioCache = result
			loadDeferred = null
		}
	}
	return portfolioCache!!
}

@Composable
fun rememberPortfolioData(): GitHubData? =
	produceState<GitHubData?>(initialValue = portfolioCache, key1 = Unit) {
		value = loadPortfolioData()
	}.value
