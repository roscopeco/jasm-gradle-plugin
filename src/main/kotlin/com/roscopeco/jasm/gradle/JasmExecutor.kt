package com.roscopeco.jasm.gradle

import com.roscopeco.jasm.JasmAssembler
import org.gradle.process.internal.worker.RequestHandler
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths

internal class JasmExecutor : RequestHandler<JasmSpec, JasmResult> {

    override fun run(request: JasmSpec): JasmResult {
        request.sourceFiles.forEach {

            val relativeSource = relativeSourceFilename(it.absolutePath, request.sourceSetDirectories)
            val code = JasmAssembler(it.name) { FileInputStream(Paths.get(it.absolutePath).toFile()) }.assemble()
            val outputPath = Paths.get(request.outputDirectory.absolutePath, relativeSource)

            outputPath.parent.toFile().mkdirs()

            Files.write(Paths.get(request.outputDirectory.absolutePath, fixClassExtension(relativeSource)), code)
        }

        return JasmResult("All looks good here...")
    }

    private fun fixClassExtension(input: String) = with (File(input)) {
        Paths.get(parent ?: "", "$nameWithoutExtension.class").toString()
    }

    // TODO this is _probably_ overkill, there's only ever one element in the set AFAICT...
    private fun relativeSourceFilename(fullPath: String, prefixes: Set<File>): String {
        var relative = fullPath

        prefixes.forEach { relative = relative.removePrefix(it.absolutePath) }

        return relative.removePrefix("/")
    }
}