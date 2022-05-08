package com.roscopeco.jasm.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.plugins.ide.idea.IdeaPlugin
import java.io.File
import javax.inject.Inject

class JasmPlugin @Inject constructor (private val objectFactory: ObjectFactory) : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(JavaLibraryPlugin::class.java)

        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all { sourceSet -> // for each source set we will:
            // Add a new 'jasm' virtual directory mapping
            val jasmDirectoryDelegate =
                JasmSourceVirtualDirectoryImpl((sourceSet as DefaultSourceSet).displayName, objectFactory)

            // TODO this is deprecated, not the right way to go in the modern world...
            DslObject(sourceSet).convention.plugins["jasm"] = jasmDirectoryDelegate

            sourceSet.getExtensions().add(
                JasmSourceDirectorySet::class.java,
                "jasm",
                jasmDirectoryDelegate.jasm
            )

            val srcDir = "src/" + sourceSet.getName() + "/jasm"
            jasmDirectoryDelegate.jasm.srcDir(srcDir)
            sourceSet.getAllSource().source(jasmDirectoryDelegate.jasm)

            // create a JasmTask for this sourceSet
            val taskName = sourceSet.getTaskName("assemble", "Jasm")

            // Set up the Jasm output directory (adding to java compile + runtime classpaths)
            val outputDirectoryName = project.buildDir.toString() + "/classes/jasm/" + sourceSet.getName()
            val outputDirectory = File(outputDirectoryName)

            val outputDirectoryFileCollection = objectFactory.fileCollection()
            outputDirectoryFileCollection.setFrom(outputDirectory.path)

            sourceSet.compileClasspath = sourceSet.compileClasspath.plus(outputDirectoryFileCollection)
            sourceSet.runtimeClasspath = sourceSet.runtimeClasspath.plus(outputDirectoryFileCollection)

            project.tasks.register(taskName, JasmTask::class.java) { jasmTask ->
                jasmTask.description = "Compiles the " + sourceSet.getName() + " Jasm sources"
                // TODO this isn't right in modern Gradle...
                //      set up convention mapping for default sources
                jasmTask.source = jasmDirectoryDelegate.jasm
                jasmTask.outputDirectory = outputDirectory
            }

            // Have Jasm should be run before compiling Java so the assembled classes are available to javac
            project.tasks.named(sourceSet.getCompileJavaTaskName()) { task -> task.dependsOn(taskName) }
        }
    }
}