plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.18.5")
    implementation("net.bytebuddy:byte-buddy-agent:1.18.5")
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveFileName.set("teamcity-agent.jar")
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("output"))

    manifest {
        attributes(
            "Premain-Class" to "home.lab.teamcity.Main",
            "Agent-Class" to "home.lab.teamcity.Main",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
    }
}
