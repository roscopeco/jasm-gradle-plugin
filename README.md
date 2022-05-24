## JASM plugin for Gradle

### What?

[JASM](https://github.com/roscopeco/jasm) is an assembler for JVM bytecode. 

This project contains a Gradle plugin that allows you to integrate JASM code into your Gradle builds
alongside Java and other languages. 

It's _very_ early days for this right now, and it's pretty much hacked together just enough to work. It definitely
doesn't follow current Gradle best practice in a number of areas but it _does_ work well enough to assemble 
JASM code as part of a Gradle project.

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

### How?

Because it's so early in development this plugin isn't released to any plugin repositories yet,
so the first thing you'll need to do if you want to use this is publish it to your local Maven repository:

```shell
./gradlew publishToMavenLocal
```

Once that's done, you should be able to create a new Gradle project. In that, add
the following to `settings.gradle.kts` (N.B **not** `build.gradle.kts`!)

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

You should then be able to include the plugin as normal in `build.gradle.kts`:

```kotlin
plugins {
    java
    id("com.roscopeco.jasm") version "0.1-SNAPSHOT"
}
```

There's an example project here which illustrates how things should be laid out: https://github.com/roscopeco/jasm-example

### Who?

JASM is copyright 2022 Ross Bamford (roscopeco AT gmail DOT com). 

See LICENSE.md for the gory legal stuff (spoiler: MIT).