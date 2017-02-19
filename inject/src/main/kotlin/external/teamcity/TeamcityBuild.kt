package external.teamcity

import util.Url

class TeamCityBuild(val scheme: String,
                    val host: String,
                    val buildId: String,
                    val buildTypeId: String) {

    fun getJacocoCoverageUrl(javaPackage: String, file: String): String {
        return Url(scheme, host, getCoveragePath(javaPackage, file)).toString()
    }

    fun getCoveragePath(javaPackage: String, file: String): String {
        return "repository/download/$buildTypeId/$buildId:id/.teamcity/coverage_jacoco/coverage.zip!/$javaPackage/$file.html"
    }

    companion object {

        fun fromUrl(url: Url): TeamCityBuild? {
            val buildId = url.params["buildId"] ?: return null
            val buildTypeId = url.params["buildTypeId"] ?: return null

            return TeamCityBuild(url.scheme, url.host, buildId, buildTypeId)
        }

    }

}