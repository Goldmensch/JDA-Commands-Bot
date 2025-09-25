plugins {
    // Apply the java-library plugin for API and implementation separation.
    id("jdacbot.convention.java")
    id("application")
}

repositories {
    maven("https://repo.jenkins-ci.org/public/")
}

dependencies {
    implementation("io.github.kaktushose:jda-commands:4.0.0-beta.8")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("commons-codec:commons-codec:1.18.0")
}

application {
    mainModule = "dev.goldmensch.jdacbot"
    mainClass = "dev.goldmensch.jdacbot.Main"
}

description = "The discord bot for the JDA-Commands support server"
