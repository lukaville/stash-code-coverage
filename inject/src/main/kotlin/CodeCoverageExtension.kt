import config.Project
import external.bitbucket.BitbucketServerApi
import external.bitbucket.PullRequestPage
import external.jacoco.JacocoParser
import external.teamcity.TeamCityBuild
import util.Url
import util.requests

class CodeCoverageExtension(private val project: Project) {

    private lateinit var page: PullRequestPage

    private lateinit var build: TeamCityBuild

    fun onPageLoaded(page: PullRequestPage) {
        this.page = page

        BitbucketServerApi.getBuilds(page.pullRequest) {
            val url = Url(it.results.first().url)
            build = TeamCityBuild.fromUrl(url) ?: return@getBuilds
        }

        page.addFileViewListener { file, path ->
            onFileLoaded(file, path)
        }
    }

    private fun onFileLoaded(file: String, path: String) {
        if (isFileMatches(file)) {
            loadCoverageReport(file, path)
        }
    }

    private fun loadCoverageReport(file: String, path: String) {
        val javaPackage = getJavaPackage(path)
        val coverageUrl = build.getJacocoCoverageUrl(javaPackage, file)
        requests.get(coverageUrl, {
            val coverage = JacocoParser.parseHtmlReport(it)
            console.log(coverage)
        })
    }

    private fun getJavaPackage(path: String): String {
        val sourceDirectories = project.sourceDirectories
        val sourceDirectory = sourceDirectories.firstOrNull { path.startsWith(it) }
        val packageRoot = if (sourceDirectory != null) {
            path.removePrefix(sourceDirectory)
        } else {
            path
        }

        return packageRoot.trim('/').replace("/", ".")
    }

    private fun isFileMatches(file: String): Boolean {
        project.includeFiles.firstOrNull { file.matches(it) } ?: return false
        project.excludeFiles.firstOrNull { file.matches(it) } ?: return true
        return false
    }

}