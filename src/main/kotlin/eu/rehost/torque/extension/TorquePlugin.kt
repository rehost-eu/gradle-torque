package eu.rehost.torque.extension

import eu.rehost.torque.tasks.GenerateOm
import eu.rehost.torque.tasks.GenerateSql
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.language.base.plugins.LifecycleBasePlugin

interface TorqueExtension {
    val sourceDir: DirectoryProperty

    val outputDir: DirectoryProperty
    val outputModifiableDir: DirectoryProperty

    val outputSqlDir: DirectoryProperty

    val outputModifiableBeanDir:DirectoryProperty
    val outputBaseBeanDir:DirectoryProperty

}

class TorquePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        val group = "torque"
        // println("Applying torque plugin!")
        val extension = project.extensions.create("torque", TorqueExtension::class.java)
        extension.sourceDir.convention(project.layout.projectDirectory.dir("src/main/schema")) // we want to use data from xml schemata
        extension.outputDir.convention(project.layout.buildDirectory.dir("src/main/java")) // directory for sources we don't want to touch, will be added to sourceSets
        extension.outputModifiableDir.convention(project.layout.projectDirectory.dir("src/main/java")) // directory for sources we want to touch, normally included in sourceSets
        extension.outputBaseBeanDir.convention(project.layout.buildDirectory.dir("src/main/java"))
        extension.outputModifiableBeanDir.convention(project.layout.projectDirectory.dir("src/main/java"))
        extension.outputSqlDir.convention(project.layout.buildDirectory.dir("sql-schema")) // normally used for copy to distribution packages

        project.plugins.withType(JavaPlugin::class.java) {
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            main.java.srcDir(extension.outputDir.get())
        }

        val generateOmTask = project.tasks.register("generateOm") {

        }

        val generateSqlTask = project.tasks.register("generateSql") {

        }

        // we configure the generation variables here
        project.tasks.withType(GenerateOm::class.java).configureEach {
            task ->
            task.group = group
            task.description = "Generates the Object Model (OM) classes."
            // path conventions
            task.sourceDir.set(extension.sourceDir.get())
            task.outputDir.set(extension.outputDir.get())
            task.outputModifiableDir.set(extension.outputModifiableDir.get())
            // defaults
            task.addGetByNameMethods.set(true)
            task.addSaveMethods.set(true)
            task.saveException.set("Exception")
            task.trackModified.set(true)
            task.trackNew.set(true)
            task.addTimeStamp.set(true)
            task.omPackage.set("org.example.Torque") // we need to set a default package, otherwise plugin will fail. This is normally configured by user
            task.peerPackageSuffix.set("")
            task.dbObjectPackageSuffix.set("")
            task.mapPackageSuffix.set(".map")
            task.managerPackageSuffix.set(".manager")
            task.beanPackageSuffix.set(".bean")
            task.baseDbObjectPackageSuffix.set("")
            task.basePeerPackageSuffix.set("")
            task.baseManagerPackageSuffix.set(".manager.base")
            task.baseBeanPackageSuffix.set(".bean")
            task.baseDbObjectClassNamePrefix.set("Base")
            task.basePeerClassNamePrefix.set("Base")
            task.useManagers.set(false)
            task.complexObjectModel.set(true)
            task.objectIsCaching.set(true)
            task.silentDbFetch.set(true)
            task.useIsForBooleanGetters.set(false)
            task.generateBeans.set(false)
            task.beanClassNameSuffix.set("Bean")
            task.beanExtendsClass.set("")

        }

        project.tasks.withType(GenerateSql::class.java).configureEach {
            task ->
            task.group = group
            task.description = "Generates the SQL schema files."
            // path conventions
            task.sourceDir.set(extension.sourceDir.get())
            task.outputSqlDir.set(extension.outputSqlDir.get())
            // defaults
            task.torqueDatabase.set("mysql")
            task.generateDrops.set(true)
        }

        project.afterEvaluate {
            val generateOmTasks = project.tasks.withType(GenerateOm::class.java).toList()
            val generateSqlTasks = project.tasks.withType(GenerateSql::class.java).toList()

            // Now we can safely add dependencies to the generateOm task
            generateOmTasks.forEach { task ->
                generateOmTask.get().dependsOn(task)
            }
            // Now we can safely add dependencies to the generateOm task
            generateSqlTasks.forEach { task ->
                generateSqlTask.get().dependsOn(task)
            }
        }

    }
}