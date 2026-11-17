import java.io.File

private const val RESULT_JSON = "result.json"

suspend fun main() {
	println("Getting data from GitHub...")

	val ayfri = GitHubAPI.getUser()
	println("Got user '${ayfri.name}'.")

	val repos = GitHubAPI.getRepositories().sortedWith(compareBy({ it.owner.login }, { it.private }, { it.archived }, { it.name }))
	println("Got ${repos.size} repos with their commits, contributors and watchers counts and their README.")

	File(RESULT_JSON).writeText(GitHubAPI.json.encodeToString(Snapshot(ayfri, repos)))
	println("Wrote result to file '$RESULT_JSON'.")
}
