package com.uselessmnemonic.gradle.jasm

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.internal.file.Deleter
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.process.internal.worker.WorkerProcessFactory
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.objectweb.asm.Opcodes
import java.io.File
import javax.inject.Inject

/**
 * A task which compiles jasm files.
 */
abstract class JasmCompile : DefaultTask(), JasmLanguageVersionable {

    /**
     * The target Java language level for assembling classfiles.
     */
    @get:Nested
    final override val languageVersion: Property<JavaLanguageVersion> = project.objects.property(JavaLanguageVersion::class.java)

    /**
     * The sources for incremental change detection.
     */
    @Incremental
    @IgnoreEmptyDirectories
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    val sources: ConfigurableFileCollection = project.objects.fileCollection()

    @get:OutputDirectory
    val destinationDirectory: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        // validate target classfile version
        val classFormat = when(this.languageVersion.get().toString()) {
            "1" -> Opcodes.V1_1
            "2" -> Opcodes.V1_2
            "3" -> Opcodes.V1_3
            "4" -> Opcodes.V1_4
            "5" -> Opcodes.V1_5
            "6" -> Opcodes.V1_6
            "7" -> Opcodes.V1_7
            "8" -> Opcodes.V1_8
            "9" -> Opcodes.V9
            "10" -> Opcodes.V10
            "11" -> Opcodes.V11
            "12" -> Opcodes.V12
            "13" -> Opcodes.V13
            "14" -> Opcodes.V14
            "15" -> Opcodes.V15
            "16" -> Opcodes.V16
            "17" -> Opcodes.V17
            "18" -> Opcodes.V18
            "19" -> Opcodes.V19
            else -> throw IllegalArgumentException("unsupported level $languageVersion")
        }

        // get files that need (re)assembly
        val destinationDirectory = this.destinationDirectory.get()
        val assemblyJobs = inputChanges.getFileChanges(sources).mapNotNull {
            if (it.fileType == FileType.DIRECTORY) return@mapNotNull null
            val targetFile = destinationDirectory.file(it.normalizedPath).asFile.run {
                File(parentFile, "$nameWithoutExtension.class")
            }
            if (it.changeType == ChangeType.REMOVED) {
                deleter.delete(targetFile)
                return@mapNotNull null
            }
            JasmCompileRequestHandler.Spec(it.file, targetFile, classFormat)
        }

        if (assemblyJobs.isEmpty()) return

        // create the worker that will dispatch the assembly jobs
        val worker = workerProcessBuilderFactory.multiRequestWorker(JasmCompileRequestHandler::class.java).run {
            sharedPackages("com.roscopeco.jasm")
            javaCommand.apply {
                workingDir = project.layout.projectDirectory.asFile
                redirectErrorStream()
            }
            build()
        }

        // TIME TO GO BOOM!
        worker.start()
        assemblyJobs.forEach(worker::run)
        worker.stop()
    }

    @get:Inject
    protected abstract val workerProcessBuilderFactory: WorkerProcessFactory

    @get:Inject
    protected abstract val deleter: Deleter
}
