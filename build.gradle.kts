plugins {
    java
}

repositories {
    jcenter()
}

dependencies {
    testImplementation("com.github.org-arl:fjage:1.7.0")
    testImplementation("junit:junit:4.13")

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    testLogging {
        events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
                org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR)
    }
}
