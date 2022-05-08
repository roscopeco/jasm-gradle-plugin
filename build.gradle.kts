import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.6.10"
}

group = "com.roscopeco.jasm"
version = "0.1-SNAPSHOT"

gradlePlugin {
    plugins {
        create("jasmPlugin") {
            id = "com.roscopeco.jasm"
            implementationClass = "com.roscopeco.jasm.gradle.JasmPlugin"
        }
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "17"
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.ow2.asm:asm:9.3")
    implementation("com.roscopeco.jasm:jasm:0.1-SNAPSHOT")
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
