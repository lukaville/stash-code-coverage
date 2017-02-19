package jacoco

import model.FileCoverage
import model.LineCoverage

object JacocoParser {

    fun parseHtmlReport(report: String): FileCoverage {
        val map = mutableMapOf<Int, LineCoverage>()
        html.lines().forEachIndexed { i, line ->
            val coverage = parseJacocoCoverageLine(line) ?: return@forEachIndexed
            map.put(i, coverage)
        }
        return FileCoverage(map)
    }

}