rootProject.name = "asyncmc"

listOf("protocol/bedrock", "protocol/java", "core")
    .forEach(::includeBuild)
