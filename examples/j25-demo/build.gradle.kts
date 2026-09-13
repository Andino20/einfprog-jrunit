plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(project(":lib"))
}

tasks.named<Test>("test") {
    useJUnitPlatform{
        excludeTags("failing")
    }
}

tasks.register<Test>("testFailing") {
    description = "Runs failing demo tests to show error formatting."
    group = "verification"

    testClassesDirs = tasks.named<Test>("test").get().testClassesDirs
    classpath = tasks.named<Test>("test").get().classpath

    useJUnitPlatform {
        includeTags("failing")
    }

    ignoreFailures = true
}