package eu.rehost.plugins

import eu.rehost.tasks.CompileReport
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty

interface JasperReportsExtension {
    val source: DirectoryProperty
    val output: DirectoryProperty
}

class JasperReportsPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        // println("Applying jasperreports plugin!")
        val extension = project.extensions.create("jasper", JasperReportsExtension::class.java)
        extension.source.convention(project.layout.projectDirectory.dir("src/main/schema"))
        extension.output.convention(project.layout.buildDirectory.dir("src/main/java"))

        // Create a custom configuration to specify the desired version of jasperreports to use during compilation
        val compilerDeps = project.configurations.create("compileReports") {
            conf ->
            conf.isVisible = false
            conf.isCanBeConsumed = false
            conf.isCanBeResolved = true
            conf.description = "The specific Torque Version for the compilation task."
            conf.defaultDependencies {
                deps ->
                deps.add(project.dependencies.create("org.apache.torque:torque-generator:5.1"))
            }
        }

        val defaultTask = project.tasks.register("generateOm", GenerateOm::class.java) {
            task ->
            task.inputFiles.from(extension.source.get())
            task.outputDir.set(extension.output.get())
        }
    }
    
}