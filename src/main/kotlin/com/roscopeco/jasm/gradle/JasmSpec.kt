package com.roscopeco.jasm.gradle

import java.io.File
import java.io.Serializable

internal data class JasmSpec(
    val sourceFiles: Set<File>,
    val sourceSetDirectories: Set<File>,
    val outputDirectory: File
) : Serializable