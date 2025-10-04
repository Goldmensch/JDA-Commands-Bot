plugins {
    // Apply the java-library plugin for API and implementation separation.
    id("jdacbot.convention.java")
    id("application")
}

repositories {
    maven("https://central.sonatype.com/repository/maven-snapshots/")
}

dependencies {
    implementation("io.github.kaktushose:jda-commands:4.0.0-SNAPSHOT")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("commons-codec:commons-codec:1.18.0")

    implementation("de.chojo.sadu:sadu-datasource:2.3.3")
    implementation("de.chojo.sadu:sadu-sqlite:2.3.3")
    implementation("de.chojo.sadu:sadu-queries:2.3.3")
    runtimeOnly("org.xerial", "sqlite-jdbc", "3.50.3.0")
}

application {
    mainModule = "dev.goldmensch.jdacbot"
    mainClass = "dev.goldmensch.jdacbot.Main"
}

java {
    modularity.inferModulePath = true
}

description = "The discord bot for the JDA-Commands support server"
