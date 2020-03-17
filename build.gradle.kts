plugins {
    kotlin("jvm") version "1.3.70"
    maven
}

group = "com.github.cyanpowered"
version = "1.0.0"

repositories {
    mavenLocal()
    jcenter()
    maven { setUrl("https://jitpack.io/") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.netty", "netty-all", "4.1.47.Final")
    implementation("org.slf4j", "slf4j-simple", "1.7.30")
}