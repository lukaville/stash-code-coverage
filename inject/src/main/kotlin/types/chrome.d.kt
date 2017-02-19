package types

@native
fun encodeURIComponent(str: String): String = noImpl

@native
object chrome {
    @native
    object tabs {
        @native
        public class Tab {
            var status: String?
            var index: Number
            var openerTabId: Number?
            var title: String?
            var url: String?
            var pinned: Boolean
            var highlighted: Boolean
            var windowId: Number
            var active: Boolean
            var favIconUrl: String?
            var id: Number
            var incognito: Boolean
        }

        fun query(queryInfo: Any, callback: (result: Array<Tab>) -> Unit): Unit = noImpl
    }
}