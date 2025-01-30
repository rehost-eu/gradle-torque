plugins {
	kotlin("jvm") version "2.1.10"
	id("com.gradle.plugin-publish") version "1.3.0"
}

repositories {
	mavenCentral()

}

// Use java-gradle-plugin to generate plugin descriptors and specify plugin ids
gradlePlugin {
	website.set("https://git.rehost.eu/rehost/gradle-plugins/gradle-torque")
	vcsUrl.set("https://git.rehost.eu/rehost/gradle-plugins/gradle-torque.git")
	plugins {
		create("TorquePlugin") {
			id = "eu.rehost.torque"
			description = "Uses the Apache Torque Generator to generate ObjectMapper classes (om) and SQL Files (sql)"
			implementationClass = "eu.rehost.torque.extension.TorquePlugin"
			displayName = "Gradle Torque Plugin"
			tags.set(listOf("torque"))
		}
	}
}

dependencies {
	implementation("org.apache.torque:torque-generator:6.0")
	implementation("org.apache.torque:torque-templates:6.0")
}