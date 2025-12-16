plugins {
    id("java")
}

group = "de.phl.programmingproject"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.beanshell:bsh-core:2.0b4")
    testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")
    mockitoAgent("org.mockito:mockito-core:5.20.0") { isTransitive = false }
}

tasks.test {
    useJUnitPlatform()
    doFirst {
        jvmArgs = (jvmArgs ?: emptyList()).toMutableList().apply {
            add("-javaagent:${mockitoAgent.singleFile.absolutePath}")
        }
    }
}

// set Java to 21 for compatibility with GitHub Classroom Autograding
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}