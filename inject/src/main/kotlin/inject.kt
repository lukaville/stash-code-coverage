import bitbucket.stash
import util.Url
import util.requests

fun main(args: Array<String>) {
    val pullRequest = stash.getPullRequest() ?: return

    val builds = stash.api.getBuilds(pullRequest)
    val buildUrl = Url(builds.results.first().url)
    val buildId = buildUrl.params["buildId"]!!
    val buildTypeId = buildUrl.params["buildTypeId"]!!


    stash.onNewFileLoaded { path, file ->
        val javaPackage = getJavaPackage(path)
        val coveragePath = getCoveragePath(buildTypeId, buildId, javaPackage, file)
        val coverageUrl = Url(buildUrl.scheme, buildUrl.host, coveragePath)
        console.log(coverageUrl.toString())
        val lines = requests.getString(coverageUrl.toString())
        console.log(lines)
    }

}

private fun getJavaPackage(path: String): String {
    val sourceDirectories = arrayOf("")
    val sourceDirectory = sourceDirectories.firstOrNull { path.startsWith(it) }
    val packageRoot = if (sourceDirectory != null) {
        path.removePrefix(sourceDirectory)
    } else {
        path
    }

    return packageRoot.trim('/').replace("/", ".")
}

fun getCoveragePath(buildTypeId: String, buildId: String, javaPackage: String, file: String): String {
    return "repository/download/$buildTypeId/$buildId:id/.teamcity/coverage_jacoco/coverage.zip!/$javaPackage/$file.html"
}