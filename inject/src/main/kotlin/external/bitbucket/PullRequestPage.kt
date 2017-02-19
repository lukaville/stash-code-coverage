package external.bitbucket

import config.Bitbucket
import external.bitbucket.model.PullRequest
import external.bitbucket.model.PullRequestResponse
import org.w3c.dom.MutationObserver
import org.w3c.dom.Node
import types.MutationObserverOptions
import kotlin.browser.document
import kotlin.dom.asList

class PullRequestPage {

    private var lastFile: String? = null

    val pullRequest: PullRequest
        get() = getPageResponse()?.pullRequestJSON ?: throw IllegalStateException("Pull request JSON not found")

    fun addFileViewListener(callback: (file: String, path: String) -> Unit) {
        val contentSelector = getPageResponse()?.contentSelector ?: return console.error("Content selector not found")
        val target = document.querySelector(contentSelector)
        val observer = MutationObserver { mutations, observer ->
            if (!isFileLoaded()) {
                return@MutationObserver
            }

            val file = getFileName() ?: return@MutationObserver
            val path = getFilePath() ?: return@MutationObserver

            val fullPath = path + file
            if (lastFile != fullPath) {
                lastFile = fullPath
                callback(file, path)
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

    private fun isFileLoaded(): Boolean {
        return document.querySelector(".diff-editor") != null
    }

    @native("response")
    private var response: PullRequestResponse

    private fun getPageResponse(): PullRequestResponse? {
        val script = getPullRequestScript() ?: return null
        val setResponse = "this.response = ${extractObject(script)};"
        eval(setResponse)
        return response
    }

    companion object {

        fun match(bitbucket: Bitbucket): Boolean {
            val location = document.location ?: return false
            if (location.host == bitbucket.host) {
                val pullRequestUrl = location.pathname.match("/projects/(.*)/repos/(.*)/pull-requests/.*")
                if (pullRequestUrl.size == 3) {
                    val project = pullRequestUrl[1]
                    val repository = pullRequestUrl[2]
                    return project == bitbucket.project && repository == bitbucket.repository
                }
            }
            return false
        }

    }
}