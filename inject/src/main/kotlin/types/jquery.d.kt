package types;

import jquery.JQuery

@native("jQuery")
fun jq(selector: String): JQuery = JQuery();