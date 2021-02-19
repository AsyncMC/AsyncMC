/*
 *     AsyncMC - A fully async, non blocking, thread safe and open source Minecraft server implementation
 *     Copyright (C) 2020 joserobjr@gamemods.com.br
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import java.util.*

plugins {
    application
    `maven-publish`
}

val isSnapshot = version.toString().endsWith("SNAPSHOT")

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
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
    create<Jar>("distJar") {
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
        
        destinationDirectory.set(distZip.get().destinationDirectory)
        archiveClassifier.set("fat")
    }
    
    assembleDist {
        dependsOn("distJar")
    }

    create<Jar>("sourceJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    withType<Jar>().configureEach {
        from(projectDir) {
            include("LICENSE.txt")
            include("NOTICE.md")
        }
    }
}


fun findProp(name: String) = findProperty(name)?.toString()?.takeIf { it.isNotBlank() }
    ?: System.getenv(name.replace('.', '_').toUpperCase())?.takeIf { it.isNotBlank() }

publishing {
    repositories {
        maven {
            val prefix = if (isSnapshot) "asyncmc.repo.snapshot" else "asyncmc.repo.release"
            url = uri(findProp("$prefix.url") ?: "$buildDir/repo")
            when(findProp("$prefix.auth.type")) {
                "password" -> credentials {
                    username = findProp("$prefix.auth.username")
                    password = findProp("$prefix.auth.password")
                }
                "aws" -> credentials(AwsCredentials::class.java) {
                    accessKey = findProp("$prefix.auth.access_key")
                    secretKey = findProp("$prefix.auth.secret_key")
                    sessionToken = findProp("$prefix.auth.session_token")
                }
                "header" -> credentials(HttpHeaderCredentials::class.java) {
                    name = findProp("$prefix.auth.header_name")
                    value = findProp("$prefix.auth.header_value")
                }
            }
        }
    }

    publications {
        create<MavenPublication>("parent") {
            from(components["java"])
            artifact(tasks["sourceJar"])
            artifact(tasks["distJar"])
            pom {
                name.set("AsyncMC")
                description.set("AsyncMC is an async, non blocking, open source, copyleft Minecraft Bedrock and Java Edition server written in Kotlin")
                url.set("https://github.com/AsyncMC/AsyncMC")
                licenses {
                    license {
                        name.set("GNU Affero General Public License, Version 3")
                        url.set("https://www.gnu.org/licenses/agpl-3.0.html")
                    }
                }
                developers {
                    developer {
                        id.set("joserobjr")
                        name.set("José Roberto de Araújo Júnior")
                        email.set("joserobjr@gamemods.com.br")
                    }
                }
                scm {
                    url.set("https://github.com/AsyncMC/AsyncMC")
                    connection.set("scm:git:https://github.com/AsyncMC/AsyncMC.git")
                    developerConnection.set("https://github.com/AsyncMC/AsyncMC.git")
                }
            }
        }
    }
}

