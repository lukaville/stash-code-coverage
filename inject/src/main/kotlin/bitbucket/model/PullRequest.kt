package bitbucket.model

class PullRequest(
        val title: String,
        val fromRef: Reference,
        val toRef: Reference
)