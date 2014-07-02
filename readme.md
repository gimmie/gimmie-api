# Gimmie Android API

This project provide Gimmie API wrapper for Java. It can be use on both Android and Normal Java
application project.

To use it, build this project with gradle and copy all jar files to
your project.

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
