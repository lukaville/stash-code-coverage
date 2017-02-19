package bitbucket

import bitbucket.model.BuildStatus
import bitbucket.model.PullRequest
import bitbucket.model.PullRequestResponse
import org.w3c.dom.MutationObserver
import org.w3c.dom.Node
import types.MutationObserverOptions
import util.requests
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.asList

object stash {

    private var lastFile: String? = null

    object api {

        private val baseUrl = "${window.location.origin}/rest"

        fun getBuilds(pullRequest: PullRequest): BuildStatus {
            val url = "$baseUrl/build-status/latest/commits/stats/${pullRequest.fromRef.latestCommit}?includeUnique=true"
            return requests.get<BuildStatus>(url)
        }

    }

    fun getPullRequest(): PullRequest? {
        return getPageResponse()?.pullRequestJSON
    }

    fun onNewFileLoaded(callback: (path: String, file: String) -> Unit) {
        val contentSelector = getPageResponse()?.contentSelector ?: return console.error("Content selector not found")
        val target = document.querySelector(contentSelector)
        val observer = MutationObserver { mutations, observer ->
            val file = getFileName() ?: return@MutationObserver
            val path = getFilePath() ?: return@MutationObserver
            val fullPath = path + file
            if (lastFile != fullPath) {
                lastFile = fullPath
                callback(path, file)
            }
        }

        val observerOptions = MutationObserverOptions(
                childList = true,
                subtree = true
        )
        observer.observe(target as Node, observerOptions)
    }

    private fun getFileName(): String? {
        return document.querySelector(".stub")?.innerHTML
    }

    private fun getFilePath(): String? {
        val pathParts = document
                .querySelectorAll(".breadcrumbs > span:not(.stub)")
                .asList()
                .map(Node::textContent)
                .filterNotNull()

        if (pathParts.isNotEmpty()) {
            return pathParts.joinToString("")
        } else {
            return null
        }
    }

    private fun getPullRequestScript() = document
            .querySelectorAll("script")
            .asList()
            .map(Node::textContent)
            .filterNotNull()
            .findLast { it.contains("pullRequestJSON") }

    private fun extractObject(script: String) = script
            .removePrefix("require('bitbucket/internal/layout/pull-request').onReady(")
            .removeSuffix(");")

    @native
    val response: PullRequestResponse

    private fun getPageResponse(): PullRequestResponse? {
        val script = getPullRequestScript() ?: return null
        val setResponse = "this.response = ${extractObject(script)};"
        eval(setResponse)
        return response
    }

}