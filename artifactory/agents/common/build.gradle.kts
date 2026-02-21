plugins {
    id("java-library")
}

dependencies {
    api("net.bytebuddy:byte-buddy:1.18.5")
    api("net.bytebuddy:byte-buddy-agent:1.18.5")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get().filter {
            it.name.endsWith("jar")
        }.map {
            zipTree(it)
        }
    })
}