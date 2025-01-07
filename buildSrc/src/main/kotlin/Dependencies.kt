import org.gradle.util.VersionNumber

object BuildPluginsVersion {
    val AGP = System.getenv("VERSION_AGP") ?: "7.4.0"
    const val KOTLIN = "1.6.10"
    const val AAR_2_JAR = "0.6"
    const val SHADOW = "7.1.2"

    // do not upgrade to 0.18.0, it does not generate the pom-default.xml and module.json under
    // build/publications/maven
    const val MAVEN_PUBLISH = "0.17.0"
    const val PROGUARD = "7.1.0"
    const val GROOVY_REDISTRIBUTED = "1.2"
}

object RepositoryUrl {
    const val snapRepositoryUrl = "scp://url"
    const val repositoryUrl = "scp://url"
}

object LibsVersion {
    const val JUNIT = "4.13.2"
    const val ASM = "7.0" // compatibility matrix -> https://developer.android.com/reference/tools/gradle-api/7.1/com/android/build/api/instrumentation/InstrumentationContext#apiversion
}

object Libs {
    fun agp(version: String) = "com.android.tools.build:gradle:$version"
    val AGP = "com.android.tools.build:gradle:${BuildPluginsVersion.AGP}"
    const val JUNIT = "junit:junit:${LibsVersion.JUNIT}"
    const val PROGUARD = "com.guardsquare:proguard-gradle:${BuildPluginsVersion.PROGUARD}"
    // this allows us to develop against a fixed version of Gradle, as opposed to depending on the
    // locally available version. kotlin-gradle-plugin follows the same approach.
    // More info: https://docs.nokee.dev/manual/gradle-plugin-development-plugin.html
    const val GRADLE_API = "dev.gradleplugins:gradle-api:7.5"

    // bytecode instrumentation
    const val ASM = "org.ow2.asm:asm-util:${LibsVersion.ASM}"
    const val ASM_COMMONS = "org.ow2.asm:asm-commons:${LibsVersion.ASM}"

    // test
    val MOCKITO_KOTLIN = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
}
