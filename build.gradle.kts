plugins {
    id("java")
}

group = "net.lightblockcrafting"
version = "1.0.2"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    archiveBaseName.set("LightBlockCrafting")
    archiveClassifier.set("")
}

// Read by CI to check a release tag against the project version - see
// .github/workflows/release.yml.
tasks.register("printVersion") {
    group = "help"
    description = "Prints the project version (used by the CI release tag check)."
    val projectVersion = project.version.toString()
    doLast { println(projectVersion) }
}
