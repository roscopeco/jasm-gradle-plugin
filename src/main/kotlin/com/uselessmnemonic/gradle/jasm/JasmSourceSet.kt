package com.uselessmnemonic.gradle.jasm

import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion
import javax.inject.Inject

/**
 * Describes a jasm source set inside the project.
 */
abstract class JasmSourceSet @Inject constructor(
    private val name: String,
    project: Project
): Named, JasmLanguageVersionable {

    override fun getName() = name

    /**
     * A [SourceDirectorySet] which contains all the `.jasm` sources for this source set.
     */
    val jasm: SourceDirectorySet = project.objects.sourceDirectorySet("$name.jasm", "$name jasm source")

    /**
     * Configures the jasm source directory set.
     */
    fun jasm(configure: SourceDirectorySet.() -> Unit) = jasm.configure()

    /**
     * Configures the jasm source directory set.
     */
    fun jasm(configure: Action<SourceDirectorySet>) = configure.execute(jasm)

    /**
     * The Java language version for which to generate classfiles.
     * It is propagated to this set's [JasmCompile] task as a default.
     */
    final override val languageVersion: Property<JavaLanguageVersion> = project.objects.property(JavaLanguageVersion::class.java)

    /**
     * The name of this source set's [JasmCompile] task.
     */
    val compileJasmTaskName: String get() {
        if (name == "main") return "compileJasm"
        else return "compile${name.replaceFirstChar(Char::uppercaseChar)}Jasm"
    }

    init {
        // whenever a source set is created, the default should be the plugin's language version
        val defaultLanguageVersion = project.providers.provider<JasmProjectExtension> {
            project.extensions.findByType(JasmProjectExtension::class.java)
        }.flatMap {
            it.languageVersion
        }
        languageVersion.convention(defaultLanguageVersion)
    }
}
