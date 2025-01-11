plugins {
    `java-library`
    id("java")
    id("io.github.0ffz.github-packages") version "1.2.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.13"
}

group = "de.dertoaster"
version = "1.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    mavenCentral()
    // Breaks paperweight
    //maven {"https://repo.dmulloy2.net/repository/public/"}
    ivy {
        name = "Github Releases - ProtocolLib"; // Github Releases
        url = uri("https://github.com");

        patternLayout {
            artifact("[organisation]/[module]/releases/download/[revision]/[module].[ext]");
        }

        metadataSources { artifact() }
    }
    maven { githubPackage("apdevteam/movecraft")(this) }
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api("org.jetbrains:annotations-java5:24.1.0")
    //compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0") // ProtocolLib
    implementation("dmulloy2:ProtocolLib:5.3.0@jar")
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly("net.countercraft:movecraft:+") // Movecraft dependency (latest version)
    compileOnly("it.unimi.dsi:fastutil:8.5.11")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION