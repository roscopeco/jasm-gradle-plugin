package com.roscopeco.jasm.gradle

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet

interface JasmSourceVirtualDirectory {
    fun getJasm(): SourceDirectorySet
    fun jasm(configureClosure: Closure<Any>): JasmSourceVirtualDirectory
    fun jasm(configureAction: Action<in SourceDirectorySet>): JasmSourceVirtualDirectory
}