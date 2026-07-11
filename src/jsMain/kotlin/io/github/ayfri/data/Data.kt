package io.github.ayfri.data

private val portfolioSnapshot: GitHubData by lazy {
	JSON.parse(portfolioSnapshotJson, ::snakeCaseReviver)
}

fun portfolioData() = portfolioSnapshot
