rootProject.name = "Alya"

val useLocalLwjglBridge = false
gradle.extra["useLocalLwjglBridge"] = useLocalLwjglBridge
if (useLocalLwjglBridge) {
    includeBuild("../LWJGL3-Bridge") {
        dependencySubstitution {
            substitute(module("org.mcphackers:legacy-lwjgl3")).using(project(":"))
        }
    }
}
