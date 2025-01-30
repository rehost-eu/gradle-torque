package eu.rehost.torque.tasks

import org.apache.torque.generator.configuration.UnitDescriptor
import org.apache.torque.generator.configuration.option.MapOptionsConfiguration
import org.apache.torque.generator.configuration.paths.CustomProjectPaths
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths
import org.apache.torque.generator.control.Controller
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

abstract class GenerateSql @Inject constructor(private val executor: WorkerExecutor) : DefaultTask() {

    @get:Input
    abstract val torqueDatabase: Property<String>

    @get:Input
    abstract val generateDrops: Property<Boolean>

    @get:InputFiles
    @get:SkipWhenEmpty
    abstract val sourceDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputSqlDir: DirectoryProperty

    @TaskAction
    fun generateSql() {
        val controller = Controller()
        val unitDescriptors: MutableList<UnitDescriptor> = ArrayList()
        val overrideOptions: MutableMap<String, String> = HashMap()
        overrideOptions["torque.database"] = torqueDatabase.get().toString()
        overrideOptions["torque.sql.generate.drops"] = generateDrops.get().toString()
        val projectPaths = CustomProjectPaths(Maven2DirectoryProjectPaths(File(".")))
        projectPaths.configurationPackage = "org.apache.torque.templates.sql"
        projectPaths.setConfigurationDir(null)
        projectPaths.setSourceDir(File(sourceDir.get().toString()))
        projectPaths.setOutputDirectory(null, File(outputSqlDir.get().toString()))
        val unitDescriptor =
            UnitDescriptor(UnitDescriptor.Packaging.CLASSPATH, projectPaths, DefaultTorqueGeneratorPaths())
        unitDescriptor.overrideOptions =
            MapOptionsConfiguration(overrideOptions)
        unitDescriptors.add(unitDescriptor)
        controller.run(unitDescriptors)

    }

    init {
        group = "build"
    }
}
