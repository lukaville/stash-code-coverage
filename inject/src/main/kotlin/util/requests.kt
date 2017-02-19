package util

import org.w3c.xhr.XMLHttpRequest

object requests {

    fun <T> getJson(url: String, callback: (T) -> Unit, error: (() -> Unit)? = null) {
        get(url, { callback(JSON.parse<T>(it)) }, error)
    }

    fun get(url: String, callback: (String) -> Unit, error: (() -> Unit)? = null) {
        val xhr = XMLHttpRequest()
        xhr.open("GET", url, async = true)
        xhr.send()
        xhr.onreadystatechange = {
            if (xhr.readyState == XMLHttpRequest.DONE) {
                if (xhr.status == 200.toShort()) {
                    callback(xhr.responseText)
                } else {
                    error?.invoke()
                }
            }
        }
    }

}