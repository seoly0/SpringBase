plugins {
    kotlin("jvm") version "1.9.22"
}

group = "me.seoly.spring"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("org.hibernate.common:hibernate-commons-annotations:6.0.6.Final")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.zeroturnaround:zt-zip:1.17")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}