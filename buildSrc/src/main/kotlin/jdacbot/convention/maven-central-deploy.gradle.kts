package jdacbot.convention

import gradle.kotlin.dsl.accessors._91ec805279bff2b5bb12917c8cd87938.jreleaser
import gradle.kotlin.dsl.accessors._91ec805279bff2b5bb12917c8cd87938.jreleaserDeploy
import gradle.kotlin.dsl.accessors._91ec805279bff2b5bb12917c8cd87938.publish
import gradle.kotlin.dsl.accessors._91ec805279bff2b5bb12917c8cd87938.publishing
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke
import org.jreleaser.model.Active

plugins {
    `maven-publish`
    id("org.jreleaser")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set(rootProject.name)
                description.set(project.description)
                url.set("https://github.com/Goldmensch/JDA-Commands-Bot")

                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("http://choosealicense.com/licenses/apache-2.0/")
                    }
                }

                developers {
                    developer {
                        name.set("Kaktushose & Goldmensch")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Goldmensch/JDA-Commands-Bot")
                    developerConnection.set("scm:git:ssh://github.com/Goldmensch/JDA-Commands-Bot")
                    url.set("https://github.com/Goldmensch/JDA-Commands-Bot")
                }
            }
        }
    }

    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    project {
        copyright = "Kaktushose & Goldmensch"
    }


    signing {
        active = Active.ALWAYS
        armored = true
    }

    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                    setStage("UPLOAD")
                }
            }
        }
    }
}

tasks.jreleaserDeploy {
    dependsOn(tasks.publish)
}