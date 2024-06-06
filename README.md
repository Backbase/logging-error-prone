# Backbase error-prone logging maven plugin

* General information about error-prone you can read [here](https://errorprone.info/) 

## How to use
1. For using this plugin you should be configured to use the new custom plugin as an annotation processor in your service:

``` java
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>${maven-compiler-plugin.version}</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.custom</groupId>
                <artifactId>LogChecking</artifactId>
                <version>1.0-SNAPSHOT</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```
2. Add the following to the .mvn/jvm.config file
``` java
--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
--add-opens jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-opens jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED
```
create ``` .mvn ``` folder inside your project root directory and then add a file name ``` jvm.config ``` with above content.


## [Logging and error handling requirements](https://backbase.atlassian.net/wiki/spaces/GUIL/pages/922386858/Logging)
Logging is an important data source for troubleshooting issues, business intelligence, and meeting compliance. Logs give records of precisely what your application is doing when.
This document aims to provide guidance on Backbase application logging requirements and error handling.