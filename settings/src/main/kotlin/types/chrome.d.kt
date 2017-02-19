package types

@native
object chrome {
    @native
    object storage {
        @native
        object sync {
            fun set(items: dynamic, callback: (() -> Unit)? = undefined)
            fun get(key: String, callback: (items: dynamic) -> Unit)
        }
    }
}

fun <T> chrome.storage.sync.setValue(key: String, value: T, callback: (() -> Unit)? = undefined) {
    val o = js("({})")
    o[key] = value
    set(o, callback)
}

fun <T> chrome.storage.sync.getValue(key: String, callback: (obj: T?) -> Unit) {
    get(key) {
        callback(it[key] as? T)
    }
}