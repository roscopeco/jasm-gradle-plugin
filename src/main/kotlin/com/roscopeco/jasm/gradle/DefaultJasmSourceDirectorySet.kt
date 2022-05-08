package com.roscopeco.jasm.gradle

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet

class DefaultJasmSourceDirectorySet(sourceDirectorySet: SourceDirectorySet ) :
    DefaultSourceDirectorySet(sourceDirectorySet), JasmSourceDirectorySet
