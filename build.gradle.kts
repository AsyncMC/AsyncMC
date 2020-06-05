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
        
        destinationDirectory.set(distZip.get().destinationDirectory)
        archiveClassifier.set("fat")
    }
    
    assembleDist {
        dependsOn("distJar")
    }
}
