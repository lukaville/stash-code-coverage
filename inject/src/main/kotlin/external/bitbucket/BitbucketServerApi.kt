package external.bitbucket

import external.bitbucket.model.BuildStatus
import external.bitbucket.model.PullRequest
import util.requests
import kotlin.browser.window

object BitbucketServerApi {

    private val baseUrl = "${window.location.origin}/rest"

    fun getBuilds(pullRequest: PullRequest, callback: (BuildStatus) -> Unit) {
        val url = "$baseUrl/build-status/latest/commits/${pullRequest.fromRef.latestCommit}"
        requests.getJson(url, callback)
    }

}