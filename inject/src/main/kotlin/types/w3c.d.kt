package types

import org.w3c.dom.MutationObserverInit

@Suppress("NOTHING_TO_INLINE")
inline fun MutationObserverOptions(childList: Boolean? = false, subtree: Boolean? = false): MutationObserverInit {
    val o = js("({})")

    o["childList"] = childList
    o["subtree"] = subtree

    return o
}