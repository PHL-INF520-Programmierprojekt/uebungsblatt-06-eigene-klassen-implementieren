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
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // Explizit auch im IDE-Test-Classpath: keine fremde Engine aus dem Runner beimischen.
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.beanshell:bsh-core:2.0b4")
    testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")
    mockitoAgent("org.mockito:mockito-core:5.20.0") { isTransitive = false }
}

// Ein fester, projektlokaler Pfad funktioniert auch im VS-Code-Test-Runner.
val prepareTestEnvironment by tasks.registering(Copy::class) {
    from(mockitoAgent)
    into(layout.buildDirectory.dir("test-agent"))
    rename { "mockito-agent.jar" }
}

tasks.test {
    useJUnitPlatform()
    dependsOn(prepareTestEnvironment)
    jvmArgs("-javaagent:${layout.buildDirectory.file("test-agent/mockito-agent.jar").get().asFile.absolutePath}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Quelltexte und Testausgaben bleiben unabhängig von der Betriebssystem-Locale deutsch lesbar.
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
}
tasks.withType<Test>().configureEach {
    systemProperty("file.encoding", "UTF-8")
    systemProperty("stdout.encoding", "UTF-8")
    systemProperty("stderr.encoding", "UTF-8")
}
