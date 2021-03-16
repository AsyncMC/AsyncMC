rootProject.name = "asyncmc"

listOf(
    "protocol/bedrock", "protocol/java", "protocol/raknet", 
    "protocol/raknet-interface", "protocol/raknet-powernukkit",
    "module-api", "core"
).forEach(::includeBuild)
