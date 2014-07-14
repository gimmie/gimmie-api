# Gimmie Java API

This project provide Gimmie API for Java application. It can be use on both Android and Normal Java project.

To use this API, build this project with gradle and copy all jar files to your project or follow this [quickstart](https://github.com/gimmie/quickstart/blob/master/java.md). 

## Upload artifacts instruction

To upload artifacts to local maven, create a new file call gradle.properties.
The content of that file look like this

```
username=<ssh username>
password=<ssh password>
releaseUrl=scp://<hostname>/path/to/maven
```

This file is ignore in .gitignore and should not upload to public repository.

After create properties file, run `gradle uploadArchives` to publish artifcacts
