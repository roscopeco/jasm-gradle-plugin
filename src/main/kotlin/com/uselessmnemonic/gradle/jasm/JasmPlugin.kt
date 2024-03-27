package com.uselessmnemonic.gradle.jasm

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.plugins.ide.idea.model.IdeaModel

abstract class JasmPlugin : Plugin<Project> {

    private inline fun Project.idea(crossinline configAction: IdeaModel.() -> Unit) {
        project.afterEvaluate { project ->
            val idea = project.extensions.findByName("idea")
            if (idea is IdeaModel) {
                idea.configAction()
            }
        }
    }

    /**
     * Some notes about what's going on here with the configuration...
     * In using the Kotlin plugin, I have come to notice some key details:
     * - many properties are created as extensions
     * - providers are used to keep up-front costs low
     * With this in mind, please take note that I follow the same principles.
     */
    override fun apply(project: Project) {

        // create the top level project extension
        val jasm = project.extensions.create("jasm", JasmProjectExtension::class.java)

        // the top-level source sets, like 'main' and 'test'
        val rootSourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer

        // we only auto-config for top-level sets
        rootSourceSets.all { sourceSet ->
            val javaCompileTaskProvider = project.tasks.named(sourceSet.compileJavaTaskName, JavaCompile::class.java)

            // there should be a jasm source set for each top-level source set
            // create here because otherwise it won't show up
            jasm.sourceSets.create(sourceSet.name) { jasmSourceSet ->
                jasmSourceSet.jasm {
                    val defaultJasmSourceRoot = project.layout.projectDirectory.dir("src/${jasmSourceSet.name}/jasm")
                    val defaultJasmDestinationDir = project.layout.buildDirectory.dir("classes/jasm/${sourceSet.name}")

                    destinationDirectory.convention(defaultJasmDestinationDir)
                    srcDir(defaultJasmSourceRoot)
                    filter.include("**/*.jasm")
                }

                // extend the top-level source set
                sourceSet.extensions.add(SourceDirectorySet::class.java, "jasm", jasmSourceSet.jasm)
                sourceSet.output.dir(jasmSourceSet.jasm.destinationDirectory)
                sourceSet.allSource.srcDirs(jasmSourceSet.jasm)
                sourceSet.compileClasspath += project.files(jasmSourceSet.jasm.destinationDirectory)
                sourceSet.runtimeClasspath += project.files(jasmSourceSet.jasm.destinationDirectory)

                // register a compile task for the set
                val jasmCompileTaskProvider = project.tasks.register(jasmSourceSet.compileJasmTaskName, JasmCompile::class.java) { jasmCompileTask ->
                    jasmCompileTask.group = "other"
                    jasmCompileTask.description = "Assembles ${jasmSourceSet.name} jasm source."
                    jasmCompileTask.sources.from(jasmSourceSet.jasm)
                    jasmCompileTask.languageVersion.convention(jasmSourceSet.languageVersion)
                }

                // mark the jasm set's compiler task, which also does some configuration
                jasmSourceSet.jasm.compiledBy(jasmCompileTaskProvider, JasmCompile::destinationDirectory)

                // make java depend on jasm
                javaCompileTaskProvider.configure { it.dependsOn(jasmCompileTaskProvider) }

                // add jasm as a dependency
                project.configurations.named(if (jasmSourceSet.name == "main") "implementation" else "${jasmSourceSet.name}Implementation") {
                    val jasmOutput = project.files(jasmSourceSet.jasm.destinationDirectory)
                    val jasmDependency = project.dependencies.create(jasmOutput)
                    it.dependencies.add(jasmDependency)
                }

                // add the output directory as a source root for IntelliJ
                project.idea {
                    module {
                        it.sourceDirs.addAll(jasmSourceSet.jasm.sourceDirectories)
                    }
                }
            }
        }
    }
}
