package model

class LineCoverage(
        val coverage: Coverage,
        val branchCoverage: Coverage?,
        val description: String?
) {
    enum class Coverage {
        FULL,
        PARTIAL,
        NONE
    }
}