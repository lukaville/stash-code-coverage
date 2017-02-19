# Bitbucket Server Code Coverage
Chrome extension that displays code coverage button on pull requests in Bitbucket Server

![Screenshot](http://i.imgur.com/Z77LWD6.png)

## Build

Run:

```bash
gradle build
```

Extension will appear in `build/extension` directory.

## Configuration

Sample configuration:

```json
{
  "projects": [
    {
      "bitbucket": {
        "project": "AA",
        "repository": "example-repository",
        "host": "stash.example.org"
      },
      "excludeFiles": [
        ".*Test\\..*"
      ],
      "includeFiles": [
        ".*\\.java",
        ".*\\.kt"
      ],
      "sourceDirectories": [
        "example/src/main/java"
      ],
      "teamCity": {
        "host": "teamcity.example.org",
        "buildTypeId": "ExampleBuildType"
      }
    }
  ]
}
```