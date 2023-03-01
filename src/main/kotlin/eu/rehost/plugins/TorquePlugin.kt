package eu.rehost.plugins

import eu.rehost.tasks.GenerateOm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty

interface TorqueExtension {
    val sourceDir: DirectoryProperty
    val outputDir: DirectoryProperty
    val outputModifiableDir: DirectoryProperty
}

class TorquePlugin: Plugin<Project> {

    override fun apply(project: Project) {
        // println("Applying torque plugin!")
        val extension = project.extensions.create("torque", TorqueExtension::class.java)
        extension.sourceDir.convention(project.layout.projectDirectory.dir("src/main/schema"))
        extension.outputDir.convention(project.layout.buildDirectory.dir("src/main/java"))
        extension.outputModifiableDir.convention(project.layout.projectDirectory.dir("src/main/java"))

        val defaultTask = project.tasks.register("generateOm", GenerateOm::class.java) {
            task ->
            task.sourceDir.set(extension.sourceDir.get())
            task.outputDir.set(extension.outputDir.get())
            task.outputModifiableDir.set(extension.outputModifiableDir.get())

        }
        // Automatically route the specified compiler files to all tasks unless the user overrides them manually
        project.tasks.withType(GenerateOm::class.java).configureEach {
                task ->
                task.torquePackage.set("torque.default")
        }

    }
}