package external.bitbucket.model

class BuildStatus(
        val failed: Int,
        val successful: Int,
        val inProgress: Int,
        val results: Array<BuildResult>
)