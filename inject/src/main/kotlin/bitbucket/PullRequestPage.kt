package bitbucket

import bitbucket.model.PullRequest
import bitbucket.model.PullRequestResponse
import org.w3c.dom.MutationObserver
import org.w3c.dom.Node
import types.MutationObserverOptions
import kotlin.browser.document
import kotlin.dom.asList

open class PullRequestPage {

    private var lastFile: String? = null

    open fun onPullRequestPageLoaded(pullRequest: PullRequest) {
    }

    open fun onNewFileLoaded(file: String, path: String) {
    }

    fun onPageLoaded() {
        val pr = getPullRequest()
        if (pr != null) {
            onPullRequestPageLoaded(pr)
            observeFileLoad()
        }
    }

    fun getPullRequest(): PullRequest? {
        return getPageResponse()?.pullRequestJSON
    }

    private fun observeFileLoad() {
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
                onNewFileLoaded(file, path)
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

    @native
    val response: PullRequestResponse

    private fun getPageResponse(): PullRequestResponse? {
        val script = getPullRequestScript() ?: return null
        val setResponse = "this.response = ${extractObject(script)};"
        eval(setResponse)
        return response
    }
}