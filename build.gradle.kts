import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.sandymist.mobile.android.gradle.internal.BootstrapAndroidSdk
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
    }
}

plugins {
    id("dev.gradleplugins.groovy-gradle-plugin") version BuildPluginsVersion.GROOVY_REDISTRIBUTED
    kotlin("jvm") version BuildPluginsVersion.KOTLIN
    id("distribution")
    id("java-gradle-plugin")
    id("groovy")
    id("maven-publish")
    // we need this plugin in order to include .aar dependencies into a pure java project, which the gradle plugin is
    id("com.stepango.aar2jar") version BuildPluginsVersion.AAR_2_JAR
    id("com.github.johnrengelman.shadow") version BuildPluginsVersion.SHADOW
    id("com.gradle.plugin-publish") version "1.3.0"
}

repositories {
    mavenCentral()
    google()
}

group = "com.sandymist.mobile"
version = "0.1.2"

BootstrapAndroidSdk.locateAndroidSdk(project, extra)

val testImplementationAar by configurations.getting // this converts .aar into .jar dependencies

val agp74: SourceSet by sourceSets.creating

val shade: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val fixtureClasspath: Configuration by configurations.creating

dependencies {
    agp74.compileOnlyConfigurationName(Libs.GRADLE_API)
    agp74.compileOnlyConfigurationName(Libs.agp("7.4.0"))

    compileOnly(Libs.GRADLE_API)
    compileOnly(Libs.AGP)
    compileOnly(agp74.output)
    compileOnly(Libs.PROGUARD)

    compileOnly(Libs.ASM)
    compileOnly(Libs.ASM_COMMONS)

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
    testImplementation(Libs.AGP)
    testImplementation(agp74.output)
    fixtureClasspath(agp74.output)
    testImplementation(Libs.PROGUARD)
    testImplementation(Libs.JUNIT)
    testImplementation(Libs.MOCKITO_KOTLIN)

    testImplementation(Libs.ASM)
    testImplementation(Libs.ASM_COMMONS)
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// We need to compile Groovy first and let Kotlin depend on it.
// See https://docs.gradle.org/6.1-rc-1/release-notes.html#compilation-order
tasks.withType<GroovyCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()

    // we don't need the groovy compile task for compatibility source sets
    val ignoreTask = name.contains("agp", ignoreCase = true)
    isEnabled = !ignoreTask
    if (!ignoreTask) {
        classpath = sourceSets["main"].compileClasspath
    }
}

tasks.withType<KotlinCompile>().configureEach {
    if (!name.contains("agp", ignoreCase = true)) {
        classpath += files(sourceSets["main"].groovy.classesDirectory)
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xjvm-default=enable")
        languageVersion = "1.4"
        apiVersion = "1.4"
    }
}

tasks.withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}

gradlePlugin {
    plugins {
        register("interceptorPlugin") {
            id = "com.sandymist.mobile.plugin.interceptor"
            implementationClass = "com.sandymist.mobile.android.gradle.InterceptorPlugin"
            displayName = "Interceptor Plugin for Android"
        }
    }
}

pluginBundle {
    website = "https://github.com/saravr/interceptor-plugin"
    vcsUrl = "https://github.com/saravr/interceptor-plugin"
    description = "Network interceptor plugin for Android"
    tags = listOf("android", "interceptor", "okhttp")
}

tasks.withType<Jar> {
    from(agp74.output)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    configurations = listOf(project.configurations.getByName("shade"))

    exclude("/kotlin/**")
    exclude("/groovy**")
    exclude("/org/**")
}

artifacts {
    runtimeOnly(tasks.named("shadowJar"))
    archives(tasks.named("shadowJar"))
}

val sep = File.separator

distributions {
    main {
        contents {
            from("build${sep}libs")
            from("build${sep}publications${sep}maven")
        }
    }
    create("interceptorPluginMarker") {
        contents {
            from("build${sep}publications${sep}interceptorPluginPluginMarkerMaven")
        }
    }
}

tasks.register<com.sandymist.mobile.android.gradle.internal.ASMifyTask>("asmify")

afterEvaluate {
    publishing {
        repositories {
            maven {
                println("VERS: " + project.version.toString())
                name = "artifactory"
                url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                    uri(RepositoryUrl.snapRepositoryUrl)
                } else {
                    uri(RepositoryUrl.repositoryUrl)
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                artifactId = "interceptor"
                from(components["java"])
            }
        }
    }
}

