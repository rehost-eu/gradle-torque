package eu.rehost.tasks

import org.apache.logging.log4j.core.tools.Generate
import org.apache.torque.generator.configuration.UnitDescriptor
import org.apache.torque.generator.configuration.option.MapOptionsConfiguration
import org.apache.torque.generator.configuration.paths.CustomProjectPaths
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths
import org.apache.torque.generator.configuration.paths.Maven2ProjectPaths
import org.apache.torque.generator.control.Controller
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileType
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.net.URLClassLoader
import javax.inject.Inject

abstract class GenerateOm @Inject constructor(private val executor: WorkerExecutor) : DefaultTask() {

    @get:Input
    abstract val torquePackage: Property<String>

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputFiles
    abstract val sourceDir: DirectoryProperty

    @get:OutputDirectories
    abstract val outputDir: DirectoryProperty

    @get:OutputDirectories
    abstract val outputModifiableDir: DirectoryProperty


    @TaskAction
    fun generateOm(changes: InputChanges) {
        changes.getFileChanges(sourceDir).forEach { change ->
            if (change.fileType == FileType.DIRECTORY) return@forEach
            else if (change.file.extension != "xml") return@forEach

            if (change.changeType == ChangeType.REMOVED) {
                //FIXME: we can't delete, as we dont know what it generates!
            } else {
                val controller = Controller()
                val unitDescriptors: MutableList<UnitDescriptor> = ArrayList()
                val overrideOptions: MutableMap<String, String> = HashMap()
                overrideOptions["torque.om.package"] = torquePackage.get().toString()
                val projectPaths = CustomProjectPaths(Maven2DirectoryProjectPaths(File(".")))
                projectPaths.configurationPackage = "org.apache.torque.templates.om"
                projectPaths.setConfigurationDir(null)
                projectPaths.setSourceDir(File(sourceDir.get().toString()))
                projectPaths.setOutputDirectory(Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY, File(outputModifiableDir.get().toString()))
                projectPaths.setOutputDirectory(null, File(outputDir.get().toString()))
                val unitDescriptor =
                    UnitDescriptor(UnitDescriptor.Packaging.CLASSPATH, projectPaths, DefaultTorqueGeneratorPaths())
                unitDescriptor.overrideOptions =
                    MapOptionsConfiguration(overrideOptions)
                unitDescriptors.add(unitDescriptor)
                controller.run(unitDescriptors)
            }
        }
    }
    init {
        group = "build"
    }

}
interface GenerateOmParameters : WorkParameters {
    val torquePackage: Property<String>
    val sourceDir: Property<File>
    val outputDir: Property<File>
    val outputModifiableDir: Property<File>

}

abstract class GenerateOmWork : WorkAction<GenerateOmParameters> {
    override fun execute() {
        val params = parameters
        val controller = Controller()
        val unitDescriptors: MutableList<UnitDescriptor> = ArrayList()
        val overrideOptions: MutableMap<String, String> = HashMap()
        overrideOptions["torque.om.package"] = params.torquePackage.get().toString()
        val projectPaths = CustomProjectPaths(Maven2DirectoryProjectPaths(File(".")))
        projectPaths.configurationPackage = "org.apache.torque.templates.om"
        projectPaths.setConfigurationDir(null)
        projectPaths.setSourceDir(File(params.sourceDir.get().toString()))
        projectPaths.setOutputDirectory(null, File(params.outputDir.get().toString()))
        projectPaths.setOutputDirectory(Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY, File(params.outputModifiableDir.get().toString()))

        val unitDescriptor =
            UnitDescriptor(UnitDescriptor.Packaging.CLASSPATH, projectPaths, DefaultTorqueGeneratorPaths())
        unitDescriptor.overrideOptions =
            MapOptionsConfiguration(overrideOptions)
        unitDescriptors.add(unitDescriptor)
        controller.run(unitDescriptors)
    }
}
