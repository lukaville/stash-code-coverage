class Config(
        val projects: Array<Project>
)

class Project(
        val bitbucket: Bitbucket,
        val teamCity: TeamCity,
        val includeFiles: Array<String>,
        val excludeFiles: Array<String>,
        val sourceDirectories: Array<String>
)

class Bitbucket(
        val url: String,
        val project: String
)

class TeamCity(
        val url: String
)