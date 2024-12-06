plugins {
    id("java-library")
}

val osNameArch : String  by rootProject.extra

dependencies {

    api(project(":core"))
    api(project(":lwjgl3.glfw"))
    api(libs.lwjgl3.opengl)
    runtimeOnly(variantOf(libs.lwjgl3.opengl.natives) { classifier(osNameArch) })
}
