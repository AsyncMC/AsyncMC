import java.util.Properties

plugins {
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
    modularity.inferModulePath.set(true)
}

application {
    mainClass.set("com.github.asyncmc.boot.AsyncMCBoot")
    mainModule.set("com.github.asyncmc.boot")
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

tasks {
    register<Jar>("distJar") {
        group = "distribution"
        dependsOn(classes, distZip)
        from(sourceSets.main.get().runtimeClasspath)
        from(zipTree(distZip.get().archiveFile.get()).matching {
            include("**/lib")
            exclude("**/"+jar.get().archiveFileName.get())
        })
        rename("^[^/]+\\.jar$", "libs/$0")

        manifest {
            attributes("Main-Class" to "com.github.asyncmc.boot.AsyncMCBoot")
        }
        
        archiveClassifier.set("all")
    }
}
