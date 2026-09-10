plugins {
    java
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
    useJUnitPlatform()
}