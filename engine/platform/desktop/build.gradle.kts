plugins {
    id("java-library")
}

dependencies {
    api(project(":core"))
    runtimeOnly(libs.logging.runtime)
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