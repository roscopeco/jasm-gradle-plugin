package com.roscopeco.jasm.gradle

import org.gradle.api.file.*
import org.gradle.api.tasks.*
import org.gradle.internal.file.Deleter
import org.gradle.process.internal.worker.WorkerProcessFactory
import org.gradle.work.ChangeType
import org.gradle.work.InputChanges
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException
import java.util.concurrent.Callable
import javax.inject.Inject

open class JasmTask : SourceTask() {
    private val stableSources: ConfigurableFileCollection = project.files(Callable<Any> { this.source })

    private var jasmClassPath: ConfigurableFileCollection? = null
    private var sourceSetDirectories: FileCollection? = null

    @OutputDirectory
    var outputDirectory: File? = null

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        val sourceFiles: MutableSet<File> = HashSet()
        val stableSources: FileCollection = getStableSources()

        if (inputChanges.isIncremental) {
            var rebuildRequired = false

            for (fileChange in inputChanges.getFileChanges(stableSources)) {
                if (fileChange.fileType == FileType.FILE) {
                    if (fileChange.changeType == ChangeType.REMOVED) {
                        rebuildRequired = true
                        break
                    }
                    sourceFiles.add(fileChange.file)
                }
            }

            if (rebuildRequired) {
                try {
                    getDeleter().ensureEmptyDirectory(outputDirectory!!)
                } catch (ex: IOException) {
                    throw UncheckedIOException(ex)
                }
                sourceFiles.addAll(stableSources.files)
            }
        } else {
            sourceFiles.addAll(stableSources.files)
        }

        val manager = JasmWorkerManager()
        val spec = JasmSpecFactory().create(this, sourceFiles, sourceSetDirectories)

        manager.runWorker(projectDir(), getWorkerProcessBuilderFactory(), jasmClassPath, spec)
    }

    private fun projectDir(): File {
        return getProjectLayout().projectDirectory.asFile
    }

    override fun setSource(source: Any) {
        super.setSource(source)
        if (source is SourceDirectorySet) {
            this.sourceSetDirectories = source.sourceDirectories
        }
    }

    @SkipWhenEmpty
    @IgnoreEmptyDirectories
    @PathSensitive(PathSensitivity.RELATIVE)
    @InputFiles
    protected fun getStableSources(): FileCollection {
        return stableSources
    }

    @Inject
    protected open fun getProjectLayout(): ProjectLayout {
        throw UnsupportedOperationException("This should have been overriden by the decorator!")
    }

    @Inject
    protected open fun getDeleter(): Deleter {
        throw UnsupportedOperationException("This should have been overriden by the decorator!")
    }

    @Inject
    protected open fun getWorkerProcessBuilderFactory(): WorkerProcessFactory {
        throw UnsupportedOperationException("This should have been overriden by the decorator!")
    }
}