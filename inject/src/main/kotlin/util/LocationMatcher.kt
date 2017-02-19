package util

import org.w3c.dom.Location

interface LocationMatcher {
    fun match(location: Location): Boolean
}