plugins {
    id("java-library")
}

val osNameArch : String  by rootProject.extra

dependencies {
    api(project(":core"))
    api(libs.lwjgl3.core)
    api(libs.lwjgl3.glfw)
    api(libs.lwjgl3.stb)
    runtimeOnly(variantOf(libs.lwjgl3.core.natives) { classifier(osNameArch) })
    runtimeOnly(variantOf(libs.lwjgl3.glfw.natives) { classifier(osNameArch) })
    runtimeOnly(variantOf(libs.lwjgl3.stb.natives) { classifier(osNameArch) })
}
