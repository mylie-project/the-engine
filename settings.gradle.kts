rootProject.name = "mylie.engine"

include("core")
project(":core").projectDir=file("engine/core")

include("platform.desktop")
project(":platform.desktop").projectDir=file("engine/platform/desktop")

include("lwjgl3.glfw")
project(":lwjgl3.glfw").projectDir=file("backends/lwjgl3/glfw")

include("lwjgl3.opengl")
project(":lwjgl3.opengl").projectDir=file("backends/lwjgl3/opengl")

include("lwjgl3.imgui")
project(":lwjgl3.imgui").projectDir=file("backends/lwjgl3/imgui")

include("examples.tests")
project(":examples.tests").projectDir=file("examples/tests")





dependencyResolutionManagement{
    versionCatalogs{
        create("libs"){
            version("lombok","1.18.34")
            version("logback","1.5.12")
            version("slf4j","2.0.16")
            version("joml","1.10.7")
            version("lwjgl3","3.3.4")
            version("imgui","1.87.6")

            library("lombok","org.projectlombok","lombok").versionRef("lombok")
            library("logging.api","org.slf4j","slf4j-api").versionRef("slf4j")
            library("logging.runtime","ch.qos.logback","logback-classic").versionRef("logback")
            library("joml","org.joml","joml").versionRef("joml")
            library("lwjgl3.imgui","io.github.spair","imgui-java-app").versionRef("imgui")
            library("lwjgl3.core","org.lwjgl","lwjgl").versionRef("lwjgl3")
            library("lwjgl3.glfw","org.lwjgl","lwjgl-glfw").versionRef("lwjgl3")
            library("lwjgl3.opengl","org.lwjgl","lwjgl-opengl").versionRef("lwjgl3")
            library("lwjgl3.stb","org.lwjgl","lwjgl-stb").versionRef("lwjgl3")
            library("lwjgl3.core.natives","org.lwjgl","lwjgl").versionRef("lwjgl3")
            library("lwjgl3.glfw.natives","org.lwjgl","lwjgl-glfw").versionRef("lwjgl3")
            library("lwjgl3.opengl.natives","org.lwjgl","lwjgl-opengl").versionRef("lwjgl3")
            library("lwjgl3.stb.natives","org.lwjgl","lwjgl-stb").versionRef("lwjgl3")

        }
    }
}