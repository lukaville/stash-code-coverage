package external.bitbucket.model

class BuildStatus(
        val failed: Int,
        val successful: Int,
        val inProgress: Int,
        val values: Array<BuildResult>
)