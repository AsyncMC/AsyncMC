rootProject.name = "asyncmc"

listOf("protocol/bedrock", "protocol/java", "protocol/raknet", "core")
    .forEach(::includeBuild)
