package external.jacoco

import coverage.FileCoverage
import coverage.LineCoverage
import org.w3c.dom.parsing.DOMParser
import kotlin.dom.asList

object JacocoParser {

    fun parseHtmlReport(report: String): FileCoverage {
        val map = mutableMapOf<Int, LineCoverage>()
        report.lines().forEachIndexed { i, line ->
            val coverage = parseLine(line) ?: return@forEachIndexed
            map.put(i, coverage)
        }
        return FileCoverage(map)
    }

    fun parseLine(line: String): LineCoverage? {
        val codeLine = if (line.contains("<pre")) {
            line.split("lang-java linenums\">").last()
        } else {
            line
        }
        val node = DOMParser().parseFromString(codeLine, "text/html")
        val span = node.querySelector("span")
        val classes = span?.classList?.asList() ?: return null
        val coverage = when {
            classes.contains("nc") -> LineCoverage.Coverage.NONE
            classes.contains("pc") -> LineCoverage.Coverage.PARTIAL
            classes.contains("fc") -> LineCoverage.Coverage.FULL
            else -> return null
        }
        val branchCoverage = when {
            classes.contains("bnc") -> LineCoverage.Coverage.NONE
            classes.contains("bpc") -> LineCoverage.Coverage.PARTIAL
            classes.contains("bfc") -> LineCoverage.Coverage.FULL
            else -> null
        }

        val title = node.querySelector("span")?.getAttribute("title")

        return LineCoverage(coverage, branchCoverage, title)
    }

}