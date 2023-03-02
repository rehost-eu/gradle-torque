package eu.rehost.plugins

import eu.rehost.tasks.GenerateOm
import eu.rehost.tasks.GenerateSql
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet

import org.gradle.api.tasks.SourceSetContainer
import java.util.*


interface TorqueExtension {
    val sourceDir: DirectoryProperty
    val outputDir: DirectoryProperty
    val outputModifiableDir: DirectoryProperty
    val outputSqlDir: DirectoryProperty
}

class TorquePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        // println("Applying torque plugin!")
        val extension = project.extensions.create("torque", TorqueExtension::class.java)
        extension.sourceDir.convention(project.layout.projectDirectory.dir("src/main/schema"))
        extension.outputDir.convention(project.layout.buildDirectory.dir("src/main/java"))
        extension.outputModifiableDir.convention(project.layout.projectDirectory.dir("src/main/java"))
        extension.outputSqlDir.convention(project.layout.projectDirectory.dir("sql-schema"))

        project.plugins.withType(JavaPlugin::class.java) {
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
            val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            main.java.srcDir(extension.outputDir.get())
        }


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
        // Automatically route the specified compiler files to all tasks unless the user overrides them manually
        project.tasks.withType(GenerateOm::class.java).configureEach {
            task ->
            task.torquePackage.set("torque.default")
        }
        project.tasks.withType(GenerateSql::class.java).configureEach {
            task ->
            task.torqueDatabase.set("mysql")
        }

    }
}