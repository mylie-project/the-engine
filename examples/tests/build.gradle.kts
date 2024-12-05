plugins {
    id("java")
    id("application")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":platform.desktop"))
    implementation(project(":lwjgl3.opengl"))
    runtimeOnly(libs.logging.runtime)
}

tasks.register<JavaExec>("runTest") {
    group = "application"
    classpath=sourceSets.main.get().runtimeClasspath
    mainClass="mylie.examples.tests.A0_HelloEngine"
}

/*tasks.register<Exec>("renderDoc") {
    group = "application"
    val javaExecTask = tasks.named<JavaExec>("run").get()
    val javaHome = javaExecTask.javaLauncher.get().metadata.installationPath.asFile.absolutePath

    commandLine = listOf(
        "C://Program Files/RenderDoc/renderdoccmd",
        "capture",
        "--wait-for-exit",
        "--working-dir", ".",
        "$javaHome/bin/java",
        "--enable-preview",
        "-classpath", sourceSets.main.get().runtimeClasspath.asPath,
        "mylie.examples.tests.desktop.DesktopLauncher",

    )
}*/