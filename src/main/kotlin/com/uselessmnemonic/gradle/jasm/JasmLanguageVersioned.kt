package com.uselessmnemonic.gradle.jasm

import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion

/**
 * Marks a type as having a target Java language level.
 */
interface JasmLanguageVersioned {

    /**
     * The targeted Java language version.
     */
    val languageVersion: Provider<JavaLanguageVersion>
}
