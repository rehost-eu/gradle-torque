package eu.rehost.tasks

import org.apache.torque.generator.configuration.UnitDescriptor
import org.apache.torque.generator.configuration.option.MapOptionsConfiguration
import org.apache.torque.generator.configuration.paths.CustomProjectPaths
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths
import org.apache.torque.generator.configuration.paths.Maven2ProjectPaths
import org.apache.torque.generator.control.Controller
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject

abstract class GenerateOm @Inject constructor(private val executor: WorkerExecutor) : DefaultTask() {

    @get:Input
    abstract val addGetByNameMethods: Property<Boolean>
    @get:Input
    abstract val addSaveMethods: Property<Boolean>
    @get:Input
    abstract val saveException: Property<String>
    @get:Input
    abstract val trackModified: Property<Boolean>
    @get:Input
    abstract val trackNew: Property<Boolean>
    @get:Input
    abstract val addTimeStamp: Property<Boolean>
    @get:Input
    abstract val omPackage: Property<String>
    @get:Input
    abstract val peerPackageSuffix: Property<String>
    @get:Input
    abstract val dbObjectPackageSuffix: Property<String>
    @get:Input
    abstract val mapPackageSuffix: Property<String>
    @get:Input
    abstract val managerPackageSuffix: Property<String>
    @get:Input
    abstract val beanPackageSuffix: Property<String>
    @get:Input
    abstract val baseDbObjectPackageSuffix: Property<String>
    @get:Input
    abstract val basePeerPackageSuffix: Property<String>
    @get:Input
    abstract val baseManagerPackageSuffix: Property<String>
    @get:Input
    abstract val baseBeanPackageSuffix: Property<String>
    @get:Input
    abstract val baseDbObjectClassNamePrefix: Property<String>
    @get:Input
    abstract val basePeerClassNamePrefix: Property<String>
    @get:Input
    abstract val useManagers: Property<Boolean>
    @get:Input
    abstract val complexObjectModel: Property<Boolean>
    @get:Input
    abstract val objectIsCaching: Property<Boolean>
    @get:Input
    abstract val silentDbFetch: Property<Boolean>
    @get:Input
    abstract val useIsForBooleanGetters: Property<Boolean>
    @get:Input
    abstract val generateBeans: Property<Boolean>
    @get:Input
    abstract val beanClassNameSuffix: Property<String>
    @get:Input
    abstract val beanExtendsClass: Property<String>

    @get:InputFiles
    @get:SkipWhenEmpty
    abstract val sourceDir: DirectoryProperty

    @get:OutputDirectories
    abstract val outputDir: DirectoryProperty

    @get:OutputDirectories
    abstract val outputModifiableDir: DirectoryProperty

    @TaskAction
    fun generateOm() {
        val controller = Controller()
        val unitDescriptors: MutableList<UnitDescriptor> = ArrayList()
        val overrideOptions: MutableMap<String, String> = HashMap()
        overrideOptions["torque.om.addGetByNameMethods"] = addGetByNameMethods.get().toString()
        overrideOptions["torque.om.save.addSaveMethods"] = addSaveMethods.get().toString()
        overrideOptions["torque.om.save.saveException"] = saveException.get().toString()
        overrideOptions["torque.om.trackModified"] = trackModified.get().toString()
        overrideOptions["torque.om.trackNew"] = trackNew.get().toString()
        overrideOptions["torque.om.addTimeStamp"] = addTimeStamp.get().toString()
        overrideOptions["torque.om.package"] = omPackage.get().toString()
        overrideOptions["torque.om.package.peerPackageSuffix"] = peerPackageSuffix.get().toString()
        overrideOptions["torque.om.package.dbObjectPackageSuffix"] = dbObjectPackageSuffix.get().toString()
        overrideOptions["torque.om.package.mapPackageSuffix"] = mapPackageSuffix.get().toString()
        overrideOptions["torque.om.package.managerPackageSuffix"] = managerPackageSuffix.get().toString()
        overrideOptions["torque.om.package.beanPackageSuffix"] = beanPackageSuffix.get().toString()
        overrideOptions["torque.om.package.baseDbObjectPackageSuffix"] = baseDbObjectPackageSuffix.get().toString()
        overrideOptions["torque.om.package.basePeerPackageSuffix"] = basePeerPackageSuffix.get().toString()
        overrideOptions["torque.om.package.baseManagerPackageSuffix"] = baseManagerPackageSuffix.get().toString()
        overrideOptions["torque.om.package.baseBeanPackageSuffix"] = baseBeanPackageSuffix.get().toString()
        overrideOptions["torque.om.package.baseDbObjectClassNamePrefix"] = baseDbObjectClassNamePrefix.get().toString()
        overrideOptions["torque.om.package.basePeerClassNamePrefix"] = basePeerClassNamePrefix.get().toString()
        overrideOptions["torque.om.package.useManagers"] = useManagers.get().toString()
        overrideOptions["torque.om.complexObjectModel"] = complexObjectModel.get().toString()
        overrideOptions["torque.om.objectIsCaching"] = objectIsCaching.get().toString()
        overrideOptions["torque.om.silentDbFetch"] = silentDbFetch.get().toString()
        overrideOptions["torque.om.useIsForBooleanGetters"] = useIsForBooleanGetters.get().toString()
        overrideOptions["torque.om.generateBeans"] = generateBeans.get().toString()
        overrideOptions["torque.om.className.beanClassNameSuffix"] = beanClassNameSuffix.get().toString()
        overrideOptions["torque.om.bean.beanExtendsClass"] = beanExtendsClass.get().toString()

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
    init {
        group = "build"
    }
}