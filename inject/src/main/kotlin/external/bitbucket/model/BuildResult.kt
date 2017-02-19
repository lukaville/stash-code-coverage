package external.bitbucket.model

class BuildResult(
        val dateAdded: Long,
        val url: String,
        val state: String
) {
    companion object {
        const val STATE_SUCCESSFUL = "SUCCESSFUL"
        const val STATE_FAILED = "FAILED"
        const val STATE_INPROGRESS = "INPROGRESS"
    }
}