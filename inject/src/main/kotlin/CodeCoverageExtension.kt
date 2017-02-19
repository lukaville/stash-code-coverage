import config.Project
import coverage.FileCoverage
import coverage.LineCoverage.Coverage.*
import external.bitbucket.BitbucketServerApi
import external.bitbucket.PullRequestPage
import external.bitbucket.model.BuildResult
import external.jacoco.JacocoParser
import external.teamcity.TeamCityBuild
import util.Url
import util.requests

class CodeCoverageExtension(private val project: Project) {

    private lateinit var page: PullRequestPage

    private var build: TeamCityBuild? = null

    fun onPageLoaded(page: PullRequestPage) {
        this.page = page

        BitbucketServerApi.getBuilds(page.pullRequest) {
            val url = findBuildUrl(it.results) ?: return@getBuilds
            val build = TeamCityBuild.fromUrl(url) ?: return@getBuilds
            if (build.buildTypeId == project.teamCity.buildTypeId) {
                this.build = build
            }
        }

        page.addFileViewListener { file, path ->
            onFileLoaded(file, path)
        }
    }

    private fun findBuildUrl(builds: Array<BuildResult>): Url? {
        return builds
                .filter { it.state == BuildResult.STATE_SUCCESSFUL }
                .map { Url(it.url) }
                .firstOrNull { it.host == project.teamCity.host }
    }

    private fun onFileLoaded(file: String, path: String) {
        if (isFileMatches(file)) {
            val javaPackage = getJavaPackage(path)
            val coverageUrl = build?.getJacocoCoverageUrl(javaPackage, file) ?: return
            loadCoverageReport(coverageUrl)
        }
    }

    private fun loadCoverageReport(coverageUrl: String) {
        requests.get(coverageUrl, {
            val coverage = JacocoParser.parseHtmlReport(it)
            page.addCoverageButton(coverageUrl)
            page.showCoverage(coverage)
            showCoverageBar(coverage)
        })
    }

    private fun showCoverageBar(coverageReport: FileCoverage) {
        val full = coverageReport.lines.values.count { it.coverage == FULL }
        val partial = coverageReport.lines.values.count { it.coverage == PARTIAL }
        val none = coverageReport.lines.values.count { it.coverage == NONE }
        val sum = (full + partial + none).toFloat()

        page.addCoverageBar(
                full / sum,
                partial / sum,
                none / sum
        )
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