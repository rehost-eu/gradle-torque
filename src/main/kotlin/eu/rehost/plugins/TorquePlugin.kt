package eu.rehost.plugins

import eu.rehost.tasks.GenerateOm
import eu.rehost.tasks.GenerateSql
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

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

        // we configure path conventions here
        project.tasks.register("generateOm", GenerateOm::class.java) {
            task ->
            task.sourceDir.set(extension.sourceDir.get())
            task.outputDir.set(extension.outputDir.get())
            task.outputModifiableDir.set(extension.outputModifiableDir.get())

        }

        project.tasks.register("generateSql", GenerateSql::class.java) {
            task ->
            task.sourceDir.set(extension.sourceDir.get())
            task.outputSqlDir.set(extension.outputSqlDir.get())

        }

        // we configure the generation variables here
        project.tasks.withType(GenerateOm::class.java).configureEach {
            task ->
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
            task.torqueDatabase.set("mysql")
            task.generateDrops.set(true)
        }

    }
}