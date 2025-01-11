plugins {
    `java-library`
    id("java")
    id("io.github.0ffz.github-packages") version "1.2.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.12"
}

group = "de.dertoaster"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven {"https://repo.dmulloy2.net/repository/public/"}
    //maven { githubPackage("apdevteam/movecraft")(this) }
}

dependencies {
    //api("org.jetbrains:annotations-java5:24.1.0")
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0") // ProtocolLib
    compileOnly("net.countercraft:movecraft:+") // Movecraft dependency (latest version)
    compileOnly("it.unimi.dsi:fastutil:8.5.11")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}