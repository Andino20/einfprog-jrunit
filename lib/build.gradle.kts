import net.ltgt.gradle.errorprone.errorprone

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    jacoco
    id("net.ltgt.errorprone") version "5.1.1"

    id("org.jreleaser") version "1.26.0"
    `maven-publish`
    signing
}

group = "io.github.andino20"

base {
    archivesName = "einfprog-jrunit"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    compileOnly(libs.junit.jupiter.api)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    testCompileOnly("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")

    errorprone("com.google.errorprone:error_prone_core:2.50.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            artifactId = "einfprog-jrunit"

            pom {
                name = "einfprog-jrunit"
                description =
                    "A Java unit-testing framework utilizing reflection and dynamic proxies, built on Junit 6."
                url = "https://github.com/Andino20/einfprog-jrunit"

                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }

                developers {
                    developer {
                        id = "Andino20"
                        name = "Andreas Schlager"
                        email = "andreas.schlager28@gmail.com"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Andino20/einfprog-jrunit.git"
                    developerConnection = "scm:git:ssh://github.com/Andino20/einfprog-jrunit.git"
                    url = "https://github.com/Andino20/einfprog-jrunit"
                }
            }
        }
    }

    repositories {
        // JReleaser picks the artifacts up from here, signs them and uploads them to Maven Central.
        maven {
            name = "staging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        // Crashes on the null checks Lombok generates for @NonNull.
        disable("StringConcatToTextBlock")
    }
}
