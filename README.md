# Gradle Torque Plugin
## Description

Provides the capability to compile Objectmodels out of torque xml schematas.

## Usage

Just apply the plugin. And set "torquePackage"
It automatically adds a compile task that compiles .xml inside "src/main/schema" to
BaseClasses - "build/src/main/java"
ModifiableClasses - "src/main/java"

You can define a custom task with custom paths.

### Installation

Using gradle with kotlin DSL...

    plugins {
      id("eu.rehost.torque") version "6.0"
    }

### Configuration

config documentation currently WIP

## Getting Help

To ask questions or report bugs, please use the [GitLab project](https://git.rehost.eu/rehost/gradle-torque/issues).


## Change Log
### 6.0 (2025-01-30)
* Major refactor
* Updated torque version to 6.0 (referencing to the version it uses to generate)

### 0.0.1 (2023-02-28)
* Initial Release

## License
This plugin is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
without warranties or conditions of any kind, either express or implied.
