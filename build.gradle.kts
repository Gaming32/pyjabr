plugins {
    java
    `java-library`
    `maven-publish`
}

group = "io.github.gaming32"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.guava:guava:33.2.1-jre")
    api("it.unimi.dsi:fastutil:8.5.13")
    api("org.slf4j:slf4j-api:2.0.13")

    testImplementation("org.slf4j:slf4j-simple:2.0.13")

    compileOnly("org.jetbrains:annotations:24.1.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
    withSourcesJar()
}

tasks.compileJava {
    options.release = 22
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        fun maven(name: String, releases: String, snapshots: String) {
            maven {
                this.name = name
                url = uri(if (version.toString().endsWith("-SNAPSHOT")) snapshots else releases)
                credentials(PasswordCredentials::class)
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }

        maven(
            "gaming32",
            "https://maven.jemnetworks.com/releases",
            "https://maven.jemnetworks.com/snapshots"
        )
    }

    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            groupId = project.group.toString()
            version = project.version.toString()

            from(components["java"])

            pom {
                name = project.name
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/Gaming32/pyjabr/blob/main/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "Gaming32"
                    }
                }
            }
        }
    }
}
