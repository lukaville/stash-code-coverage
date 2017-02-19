package util

import org.w3c.xhr.XMLHttpRequest

object requests {

    fun <T> get(url: String): T {
        val xhr = XMLHttpRequest()
        xhr.open("GET", url, async = false)
        xhr.send()
        return JSON.parse<T>(xhr.responseText)
    }

    fun getString(url: String): String {
        val xhr = XMLHttpRequest()
        xhr.open("GET", url, async = false)
        xhr.send()
        return xhr.responseText
    }

}