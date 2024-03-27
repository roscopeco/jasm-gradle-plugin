package com.uselessmnemonic.gradle.jasm

import com.roscopeco.jasm.JasmAssembler
import org.gradle.process.internal.worker.RequestHandler
import java.io.File
import java.io.Serializable
import java.nio.file.Files

internal class JasmCompileRequestHandler : RequestHandler<JasmCompileRequestHandler.Spec, JasmCompileRequestHandler.Result> {

    data class Spec(val inputFile: File, val outputFile: File, val classFormat: Int) : Serializable

    sealed interface Result: Serializable
    data object Ok : Result { private fun readResolve(): Any = Ok }
    data class Error(val message: String): Result

    override fun run(spec: Spec): Result {
        try {
            val assembler = JasmAssembler(spec.inputFile.nameWithoutExtension, spec.classFormat, spec.inputFile::inputStream)
            val result = assembler.assemble()
            spec.outputFile.parentFile.mkdirs()
            Files.write(spec.outputFile.toPath(), result)
            return Ok
        } catch (e: Throwable) {
            return Error(e.message ?: e.toString())
        }
    }
}
