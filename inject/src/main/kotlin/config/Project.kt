package config

class Project(
        val bitbucket: Bitbucket,
        val teamCity: TeamCity,
        val includeFiles: Array<String>,
        val excludeFiles: Array<String>,
        val sourceDirectories: Array<String>
)