pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "Spring-Batch-Lab"
include(
    "bombatch-simple-batch",
    "bombatch-simple-chunk-batch",
    "bombatch-test-batch",
    "one-to-many-persist",
    "bombatch-multi-processing-1",
    "bombatch-multi-processing-2"
)
