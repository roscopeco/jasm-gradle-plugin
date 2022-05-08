package com.roscopeco.jasm.gradle

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.reflect.TypeOf
import org.gradle.api.reflect.TypeOf.typeOf
import org.gradle.util.internal.ConfigureUtil

class JasmSourceVirtualDirectoryImpl(parentDisplayName: String, objectFactory: ObjectFactory) : JasmSourceVirtualDirectory {

    internal var jasm: JasmSourceDirectorySet
            = createJasmSourceDirectorySet("$parentDisplayName.jasm", "$parentDisplayName Jasm source", objectFactory)

    private fun createJasmSourceDirectorySet(
        name: String,
        displayName: String,
        objectFactory: ObjectFactory
    ): JasmSourceDirectorySet {
        val jasmSourceSet: JasmSourceDirectorySet =
            DefaultJasmSourceDirectorySet(objectFactory.sourceDirectorySet(name, displayName))
        jasmSourceSet.filter.include("**/*.jasm")
        return jasmSourceSet
    }

    override fun getJasm(): SourceDirectorySet {
        return jasm
    }

    override fun jasm(configureClosure: Closure<Any>): JasmSourceVirtualDirectory {
        ConfigureUtil.configure(configureClosure, getJasm())
        return this
    }

    override fun jasm(configureAction: Action<in SourceDirectorySet>): JasmSourceVirtualDirectory {
        configureAction.execute(getJasm())
        return this
    }

    fun getPublicType(): TypeOf<*>? {
        return typeOf(JasmSourceVirtualDirectory::class.java)
    }
}