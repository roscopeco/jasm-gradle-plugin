package com.roscopeco.jasm.gradle

import org.gradle.api.file.FileCollection
import org.slf4j.LoggerFactory
import java.io.File

internal class JasmSpecFactory {
    fun create(jasmTask: JasmTask, sourceFiles: Set<File>, sourceSetDirectories: FileCollection?): JasmSpec {
        val sourceSetDirectoriesFiles: Set<File> = if (sourceSetDirectories == null) {
            emptySet()
        } else {
            sourceSetDirectories.files
        }

        if (sourceSetDirectoriesFiles.isEmpty()) {
            LoggerFactory.getLogger("JASMSPECFACTORY").info("!!!!!!! sourceSetDirFile is empty")
        } else {
            sourceSetDirectoriesFiles.forEach {
                LoggerFactory.getLogger("JASMSPECFACTORY").info("!!!!!!! SourceSetDirFile: $it")
            }
        }

        return JasmSpec(sourceFiles, sourceSetDirectoriesFiles, jasmTask.outputDirectory!!)
    }
}