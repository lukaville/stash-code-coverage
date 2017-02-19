import config.Config
import external.bitbucket.PullRequestPage
import types.chrome
import types.getValue

fun main(args: Array<String>) {
    chrome.storage.sync.getValue<String>("config") {
        val configJson = it ?: return@getValue
        val config = JSON.parse<Config>(configJson)
        injectExtension(config)
    }
}

private fun injectExtension(config: Config) {
    config.projects.forEach {
        if (PullRequestPage.match(it.bitbucket)) {
            val extension = CodeCoverageExtension(it)
            extension.onPageLoaded(PullRequestPage())
            return
        }
    }
}