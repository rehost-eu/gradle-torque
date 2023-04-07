plugins {
	kotlin("jvm") version "1.8.20"
	id("com.gradle.plugin-publish") version "1.2.0"
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://git.rehost.eu/api/v4/projects/137/packages/maven")
	}
}

// Use java-gradle-plugin to generate plugin descriptors and specify plugin ids
gradlePlugin {
	website.set("https://git.rehost.eu/rehost/gradle-torque")
	vcsUrl.set("https://git.rehost.eu/rehost/gradle-torque.git")
	plugins {
		create("TorquePlugin") {
			id = "eu.rehost.torque"
			description = "Uses the Apache Torque Generator to generate ObjectMapper classes (om) and SQL Files (sql)"
			implementationClass = "eu.rehost.plugins.TorquePlugin"
			displayName = "Gradle Torque Plugin"
			tags.set(listOf("torque"))
		}
	}
}

dependencies {
	implementation("org.apache.torque:torque-generator:5.2-SNAPSHOT")
	implementation("org.apache.torque:torque-templates:5.2-SNAPSHOT")
}

publishing {
	repositories {
		maven {
			url = uri("https://git.rehost.eu/api/v4/projects/137/packages/maven")
			credentials {
				username = ""
				password = ""
			}
			authentication {
				create<BasicAuthentication>("basic")
			}
		}
	}
}
