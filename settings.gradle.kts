plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "einfprog-jrunit"
include("lib")
include("examples:proxy")
include("examples:j25-demo")
