package com.uselessmnemonic.gradle.jasm

import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion

/**
 * Marks a type as having a configurable target Java language level.
 */
interface JasmLanguageVersionable : JasmLanguageVersioned {
    override val languageVersion: Property<JavaLanguageVersion>
}
