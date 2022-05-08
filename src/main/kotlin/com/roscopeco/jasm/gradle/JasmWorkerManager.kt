package com.roscopeco.jasm.gradle

import org.gradle.api.file.FileCollection
import org.gradle.process.internal.worker.RequestHandler
import org.gradle.process.internal.worker.WorkerProcessFactory
import java.io.File

internal class JasmWorkerManager {
    fun runWorker(
        workingDir: File,
        workerFactory: WorkerProcessFactory,
        jasmClasspath: FileCollection?,
        spec: JasmSpec
    ): JasmResult = createWorkerProcess(workingDir, workerFactory, jasmClasspath).run(spec)

    private fun createWorkerProcess(
        workingDir: File,
        workerFactory: WorkerProcessFactory,
        jasmClasspath: FileCollection?
    ): RequestHandler<JasmSpec, JasmResult> {
        val builder = workerFactory.singleRequestWorker(JasmExecutor::class.java)

        builder.baseName = "Gradle JASM Worker"

        if (jasmClasspath != null) {
            builder.applicationClasspath(jasmClasspath)
        }

        builder.sharedPackages("com.roscopeco.jasm")
        val javaCommand = builder.javaCommand
        javaCommand.workingDir = workingDir
        javaCommand.redirectErrorStream()

        return builder.build()
    }}