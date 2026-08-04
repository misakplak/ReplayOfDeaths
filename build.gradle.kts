plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.0.2"
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("com.gradleup.shadow") version "9.2.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    implementation("org.mongodb:mongodb-driver-sync:5.6.0")
    compileOnly("org.mongodb:bson:3.4.3")
    implementation("com.mysql:mysql-connector-j:9.4.0")
    implementation("com.zaxxer:HikariCP:6.3.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.11")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
