plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

dependencies {
    implementation(project(":common"))
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveFileName.set("jira.jar")
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("output"))

    manifest {
        attributes(
            "Premain-Class" to "home.lab.atlassian.jira.Main",
            "Agent-Class" to "home.lab.atlassian.jira.Main",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
    }
}