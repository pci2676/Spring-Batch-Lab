import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("io.spring.dependency-management") version Dependencies.Versions.springDependencyManagement
	id("org.springframework.boot") version Dependencies.Versions.springBoot apply false

	kotlin("jvm") version Dependencies.Versions.kotlin
	kotlin("plugin.spring") version Dependencies.Versions.kotlin apply false
	kotlin("plugin.jpa") version Dependencies.Versions.kotlin apply false
	kotlin("kapt") version Dependencies.Versions.kotlin apply false
	kotlin("plugin.allopen") version Dependencies.Versions.kotlin
	kotlin("plugin.noarg") version Dependencies.Versions.kotlin
}

allprojects {
    apply {
        plugin("idea")
    }

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }

    group = "com.javabom"
}

val javaProjects = listOf(
    project(":bombatch-simple-batch"),
    project(":bombatch-simple-chunk-batch"),
    project(":bombatch-test-batch")
)

configure(javaProjects) {
    apply {
        plugin<JavaLibraryPlugin>()
        plugin("io.spring.dependency-management")
        plugin("org.springframework.boot")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        runtimeOnly("com.h2database:h2")
        runtimeOnly("mysql:mysql-connector-java")
        runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-jdbc")
        implementation("org.springframework.boot:spring-boot-starter-batch")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.batch:spring-batch-test")
    }

    tasks.withType<JavaCompile> {
        val javaVersion = "1.8"

        targetCompatibility = javaVersion
        sourceCompatibility = javaVersion
        options.encoding = "UTF-8"

        dependsOn("processResources")
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

}

val kotlinProjects = listOf(
	project(":one-to-many-persist"),
	project(":bombatch-multi-processing-1"),
	project(":bombatch-multi-processing-2")
)
configure(kotlinProjects) {
    apply {
        plugin<JavaLibraryPlugin>()
        plugin<KotlinPlatformJvmPlugin>()
        plugin("io.spring.dependency-management")
        plugin("org.springframework.boot")
    }

    dependencyManagement {
        imports {
            mavenBom("org.jetbrains.kotlin:kotlin-bom:${Dependencies.Versions.kotlin}")
            mavenBom("org.springframework.boot:spring-boot-dependencies:${Dependencies.Versions.springBoot}")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Dependencies.Versions.springCloud}")
        }

        dependencies {
            dependency("com.querydsl:querydsl-jpa:${Dependencies.Versions.querydsl}")
            dependencySet("io.kotest:${Dependencies.Versions.kotest}") {
                entry("kotest-runner-junit5-jvm")
                entry("kotest-assertions-core-jvm")
                entry("kotest-property-jvm")
                entry("kotest-extensions-spring-jvm")
            }
            dependency("io.mockk:mockk:${Dependencies.Versions.mockk}")
            dependencySet("io.github.microutils:${Dependencies.Versions.kotlinLogging}") {
                entry("kotlin-logging-jvm")
                entry("kotlin-logging-common")
            }
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("io.github.microutils:kotlin-logging-jvm")

        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

        testImplementation("org.springframework.boot:spring-boot-starter-test")

        /** @see <a href="https://kotest.io/">kotest</a>*/
        testImplementation("io.kotest:kotest-runner-junit5-jvm")
        testImplementation("io.kotest:kotest-assertions-core-jvm")
        testImplementation("io.kotest:kotest-property-jvm")

        /** @see <a href="https://github.com/mockk/mockk">Mock K<a/>*/
        testImplementation("io.mockk:mockk")
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    tasks.withType<KotlinCompile> {
        sourceCompatibility = "11"

        kotlinOptions {
            freeCompilerArgs.plus("-Xjsr305=strict")
            freeCompilerArgs.plus("-Xjvm-default=enable")
            freeCompilerArgs.plus("-progressive")
            freeCompilerArgs.plus("-XXLanguage:+InlineClasses")

            jvmTarget = "11"
        }

        dependsOn("processResources")
    }

    tasks.withType<Test> {
        useJUnitPlatform {
        }
    }

}

