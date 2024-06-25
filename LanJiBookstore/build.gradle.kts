plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("tech.argonariod.gradle-plugin-jimmer") version "latest.release"
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
}

group = "org.lanmao"
version = "0.0.1-SNAPSHOT"


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

jimmer {
    // 设定 jimmer 依赖版本，此处也可以使用 "latest.release" 或 "0.+" 等版本范围表达式
    version = "0.8.130"
    ormCompileOnly = true

}

buildscript {
    repositories {
        mavenLocal()
        maven {
            setUrl("https://maven.aliyun.com/repository/public/")
        }
        maven {
            setUrl("https://mirrors.huaweicloud.com/repository/maven/")
        }
        mavenCentral()
    }
}

repositories {
    mavenLocal()
    maven {
        setUrl("https://maven.aliyun.com/repository/public/")
    }
    maven {
        setUrl("https://mirrors.huaweicloud.com/repository/maven/")
    }
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //SpringSecurity
    implementation("org.springframework.boot:spring-boot-starter-security")
    //mybatis-plus
    //implementation("com.baomidou:mybatis-plus-boot-starter:3.5.3.1")
    //fastJson2
    implementation("com.alibaba.fastjson2:fastjson2:2.0.25")
    //JWT
    implementation("com.auth0:java-jwt:4.3.0")
    //邮件发送模块
    implementation("org.springframework.boot:spring-boot-starter-mail")
    //接口参数校验模块
    implementation("org.springframework.boot:spring-boot-starter-validation")
    //Redis交互模块
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    //消息队列模块
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    //对象存储Minio依赖
    implementation("io.minio:minio:8.3.9")
    //tinylog 2
    implementation("org.tinylog:tinylog-api-kotlin:2.7.0")
    implementation("org.tinylog:tinylog-impl:2.7.0")

}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
