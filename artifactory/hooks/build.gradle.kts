subprojects {
    apply(plugin = "java-library")
    layout.buildDirectory.set(rootProject.layout.buildDirectory.dir("${project.name}"))
}