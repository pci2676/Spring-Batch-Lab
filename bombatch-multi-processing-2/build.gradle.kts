import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    mainClassName = "com.javabom.bombatch.multiprocessing1.main.MultiProcessing2ApplicationKt"
}

plugins {
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    kotlin("kapt")
    id("org.springframework.boot")
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-batch")
    runtimeOnly("com.h2database:h2")

    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.hibernate:hibernate-envers")
    api("com.querydsl:querydsl-jpa")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    testImplementation("org.springframework.batch:spring-batch-test")
}
