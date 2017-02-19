package external.bitbucket.model

class BuildResult(
        val dateAdded: Long,
        val url: String,
        val state: State
) {
    enum class State {
        SUCCESSFUL
    }
}