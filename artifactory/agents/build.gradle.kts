subprojects {
    repositories {
        mavenCentral()
    }
    layout.buildDirectory.set(rootProject.layout.buildDirectory.dir("${project.name}"))
}