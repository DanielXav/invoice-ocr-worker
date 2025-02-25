plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.danielxavier"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
	maven("https://repo.spring.io/milestone")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation(platform("software.amazon.awssdk:bom:2.27.21"))
	implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.1.0"))
	implementation("software.amazon.awssdk:s3")
	implementation("software.amazon.awssdk:sqs")
	implementation("software.amazon.awssdk:textract")
	implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
	testImplementation("io.mockk:mockk:1.13.5")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
