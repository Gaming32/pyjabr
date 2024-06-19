plugins {
    java
    `java-library`
    application
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

    implementation("org.slf4j:slf4j-simple:2.0.13")

    compileOnly("org.jetbrains:annotations:24.1.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "io.github.gaming32.pyjabr.PythonMain"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

tasks.compileJava {
    options.release = 22
}

tasks.test {
    useJUnitPlatform()
}
