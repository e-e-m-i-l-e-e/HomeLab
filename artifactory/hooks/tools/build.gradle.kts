val lib: String = (findProperty("lib") as String?) ?: "${project.rootDir}/lib"

dependencies {
    implementation(fileTree(lib) {
        include(listOf(
            "jackson-core-*.jar",
            "jackson-databind-*.jar",
            "jackson-annotations-*.jar",
            "artifactory-addons-manager-*.jar",
            "jfrog-crypto-*.jar",
            "bc/normal/bcprov-jdk18on-*.jar",
            "snakeyaml-*.jar",
            "commons-codec-*.jar"
        ))
    })
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            "Main-Class" to "artifactory.tools.LicenseGenerator"
        )
    }
    from({
        configurations.runtimeClasspath.get().filter {
            it.name.endsWith("jar")
        }.map {
            zipTree(it)
        }
    }) {
        exclude("META-INF/*.SF")
    }
}
