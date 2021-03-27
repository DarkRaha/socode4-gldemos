import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
    kotlin("jvm") version "1.4.30"
}

group = "com.darkraha"
version = "1.0-SNAPSHOT"


val lwjglNatives = when (OperatingSystem.current()) {
    OperatingSystem.LINUX   -> System.getProperty("os.arch").let {
        if (it.startsWith("arm") || it.startsWith("aarch64"))
            "natives-linux-${if (it.contains("64") || it.startsWith("armv8")) "arm64" else "arm32"}"
        else
            "natives-linux"
    }
    OperatingSystem.MAC_OS  -> "natives-macos"
    OperatingSystem.WINDOWS -> "natives-windows"
    else -> throw Error("Unrecognized or unsupported Operating system. Please set \"lwjglNatives\" manually")
}


repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.darkraha.opengldemoj.OpenGLJ"
    }
}

application {
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
    mainClass.set("com.darkraha.opengldemoj.OpenGLJ")
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
val lwjglVersion = "3.2.3"


dependencies {
   // implementation(kotlin("stdlib"))
    implementation (group = "org.l33tlabs.twl", name = "pngdecoder", version = "1.0")
    implementation(kotlin("stdlib-jdk8"))
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation ("org.joml:joml:1.10.1")
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    testCompile("junit", "junit", "4.12")
}
