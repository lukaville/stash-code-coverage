import org.w3c.dom.HTMLTextAreaElement
import types.chrome
import types.getValue
import types.setValue
import kotlin.browser.document

fun main(args: Array<String>) {
    val textArea = document.querySelector("#config") as? HTMLTextAreaElement ?: return
    chrome.storage.sync.getValue<String>("config") {
        textArea.value = it ?: ""
    }
    textArea.addEventListener("input", {
        chrome.storage.sync.setValue("config", textArea.value)
    })
}