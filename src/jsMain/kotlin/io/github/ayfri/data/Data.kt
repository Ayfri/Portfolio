package io.github.ayfri.data

private val portfolioSnapshot: GitHubData by lazy {
	JSON.parse<GitHubData>(portfolioSnapshotJson, ::snakeCaseReviver)
}

fun portfolioData(): GitHubData = portfolioSnapshot
