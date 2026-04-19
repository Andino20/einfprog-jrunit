
plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
    signing
}

group = "plus.einfprog"
version = "0.1.0"

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
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])

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
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Andino20/einfprog-jrunit")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}


tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
