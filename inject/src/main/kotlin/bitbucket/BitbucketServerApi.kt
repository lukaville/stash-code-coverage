package bitbucket

import bitbucket.model.BuildStatus
import bitbucket.model.PullRequest
import util.requests
import kotlin.browser.window

object BitbucketServerApi {

    private val baseUrl = "${window.location.origin}/rest"

    fun getBuilds(pullRequest: PullRequest, callback: (BuildStatus) -> Unit) {
        val url = "$baseUrl/build-status/latest/commits/stats/${pullRequest.fromRef.latestCommit}?includeUnique=true"
        requests.getJson(url, callback)
    }

}