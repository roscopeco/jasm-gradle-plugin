package com.uselessmnemonic.gradle.jasm

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion
import javax.inject.Inject

/**
 * Container for configurable properties of the jasm plugin.
 */
abstract class JasmProjectExtension @Inject constructor(project: Project) : JasmLanguageVersionable {

    /**
     * The jasm source sets in this project.
     */
    internal val sourceSets: NamedDomainObjectContainer<JasmSourceSet> = project.objects.domainObjectContainer(JasmSourceSet::class.java)

    /**
     * The language version that this plugin will target.
     * It is propagated to other jasm [JasmLanguageVersioned] components, like [sourceSets] and [JasmCompile], as a default.
     */
    final override val languageVersion: Property<JavaLanguageVersion> = project.objects.property(JavaLanguageVersion::class.java)

    init {
        // try to detect the current toolchain's language version
        val javaExtension = project.providers.provider<JavaPluginExtension> {
            project.extensions.findByType(JavaPluginExtension::class.java)
        }
        val toolchainLanguageVersion = javaExtension.flatMap {
            it.toolchain.languageVersion
        }
        val targetLanguageVersion = javaExtension.map {
            JavaLanguageVersion.of(it.targetCompatibility.majorVersion)
        }
        languageVersion.convention(toolchainLanguageVersion.orElse(targetLanguageVersion))
        (this as ExtensionAware).extensions.add("sourceSets", sourceSets)
    }
}
