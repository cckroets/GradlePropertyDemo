import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import java.io.File

abstract class SamplePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val producerTask = project.tasks.register("produce", ProducerTask::class.java) { task ->
            task.outputFile.set(project.buildDir.resolve("produced.txt"))
        }

        // Expect that when consume is executed, produce will also be executed
        // Works as expected in 6.1, breaks in 6.2.
        project.tasks.register("consume", ConsumerTask::class.java) { task ->
            task.inputFile.set(producerTask.flatMap { it.outputFile })
        }
    }
}

abstract class ProducerTask: DefaultTask() {

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun produce(): Unit = outputFile.get().asFile.writeText("src/main/resources/sample1.txt")
}

abstract class ConsumerTask: DefaultTask() {

    @get:InputFile
    abstract val inputFile: RegularFileProperty

    /**
     * Exception thrown here. Exploded is attempted to be read by gradle to figure out the task dependencies
     * before `inputFiles` is populated
     */
    @get:InputFiles
    @get:SkipWhenEmpty
    val exploded: Provider<FileCollection> by lazy {
        inputFile.map { file ->
            project.layout.files(file.asFile.readLines().map { File(it) })
        }
    }

    @TaskAction
    fun consume(): Unit = project.logger.warn("Files: ${exploded.get().files}")
}
