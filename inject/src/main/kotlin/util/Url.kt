package util

class Url {

    val scheme: String
    val host: String
    val path: String
    val params: MutableMap<String, String> = mutableMapOf()

    constructor(scheme: String, host: String, path: String? = null, params: Map<String, String> = emptyMap()) {
        this.scheme = scheme
        this.host = host
        this.path = path ?: ""
        this.params.putAll(params)
    }

    constructor(url: String) {
        val schemeParts = url.split(SCHEME_SEPARATOR)
        val hostParts = schemeParts.last().split("/")
        val query = hostParts.last()
        val pathParts = query.split("?")
        val params = pathParts.last()

        scheme = schemeParts.first()
        host = hostParts.first()
        path = pathParts.first()

        if (params.isNotEmpty()) {
            val paramsParts = params.split("&")
            paramsParts.forEach {
                val paramParts = it.split("=")
                this.params[paramParts.first()] = paramParts.last()
            }
        }
    }

    override fun toString(): String {
        return scheme + SCHEME_SEPARATOR + host + "/" + path + params.map { "${it.key}=${it.value}" }.joinToString("&")
    }

}

private const val SCHEME_SEPARATOR = "://"