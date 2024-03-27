## JASM plugin for Gradle

### What?

[JASM](https://github.com/roscopeco/jasm) is an assembler for JVM bytecode.

This project contains a Gradle plugin that allows you to integrate JASM code into your Gradle builds
alongside Java and other languages. 

The plugin is available in the [Gradle Plugin Repository](https://plugins.gradle.org/plugin/com.uselessmnemonic.jasm-plugin)
and can be applied directly to your Gradle project without any additional download. Follow the link
for latest version information.

It's _very_ early days for this right now, and it's pretty much hacked together just enough to work. 
However, it _does_ work well enough to assemble Jasm code as part of a Gradle project.

What is currently supported:

* Adds source sets for JASM code (in `src/main/jasm` and `src/test/jasm`)
* Hooks in to the Java lifecycle (if in use) to assemble JASM code before Java compile
* Output goes into `build/classes/jasm`
* Javac and runtime (for application plugin) classpaths include JASM assembled classes

However, as this is still a WIP, there's plenty still to do:

* Allow configuration of the JASM task
* Stop using deprecated Gradle features
* Tidy up the code
* and much more

### Requirements

* Java 11 or above (for the VM running Gradle)

### How?

Add the following in your `build.gradle.kts`:

```kotlin
plugins {
    id("com.uselessmnemonic.jasm-plugin") version "0.9.0"
}
```

There's an example project here which illustrates how things should be laid out: https://github.com/roscopeco/jasm-example

### Who?

JASM is copyright 2022 Ross Bamford (roscopeco AT gmail DOT com).
2024 Christopher Madrigal (chrisjmadrigal AT gmail DOT com)

See LICENSE.md for the gory legal stuff (spoiler: MIT).
