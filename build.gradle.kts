import java.util.Properties

plugins {
    id("com.github.johnrengelman.shadow") version "5.2.0"
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
    modularity.inferModulePath.set(true)
}

application {
    mainClass.set("com.github.asyncmc.core.AsyncMCLoader")
    mainClassName = "com.github.asyncmc.core.AsyncMCLoader"
    mainModule.set("com.github.asyncmc.internal.core")
    executableDir = "run"
}

repositories {
    jcenter()
    maven(url = "https://repo.gamemods.com.br/public/")
}

dependencies {
    implementation(internal("core"))
    implementation(internal("protocol/bedrock", "bedrock-protocol"))
    implementation(internal("protocol/java", "java-protocol"))
}

fun internal(path: String, name: String = path): String {
    val props = Properties().apply {
        file("$path/gradle.properties").bufferedReader().use(::load)
    }
    return props.getProperty("group")+":$name:"+props.getProperty("version")
}
