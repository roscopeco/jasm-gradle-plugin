import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.0.0-rc-2"
    kotlin("jvm") version "1.6.10"
}

group = "com.roscopeco.jasm"
version = "0.4.0"

gradlePlugin {
    plugins {
        create("jasmPlugin") {
            id = "com.roscopeco.jasm"
            implementationClass = "com.roscopeco.jasm.gradle.JasmPlugin"
            displayName = "Jasm Plugin"
            description = "Plugin supporting the Jasm assembler"
        }
    }
}

pluginBundle {
    website = "https://github.com/roscopeco/jasm-gradle-plugin"
    vcsUrl = "https://github.com/roscopeco/jasm-gradle-plugin.git"
    tags = listOf("jasm", "assembler")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "11"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.3")
    implementation("com.roscopeco.jasm:jasm:0.4.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "com.roscopeco.jasm.gradle.plugin"
            pom {
                name.set("JASM")
                description.set("Jasm JVM Assembler plugin for Gradle")
                url.set("https://github.com/roscopeco/jasm-plugin")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/roscopeco/jasm-plugin/blob/main/LICENSE.md")
                    }
                }
                developers {
                    developer {
                        id.set("roscopeco")
                        name.set("Ross Bamford")
                        email.set("roscopeco AT gmail DOT com")
                    }
                }
                scm {
                    connection.set("https://github.com/roscopeco/jasm-plugin/blob/main/LICENSE.md")
                    developerConnection.set("git@github.com:roscopeco/jasm-plugin.git")
                    url.set("https://github.com/roscopeco/jasm-plugin")
                }
            }
        }
    }
}
