package com.sandymist.mobile.android.gradle.util

import com.sandymist.mobile.android.gradle.services.InterceptorModulesService
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.provider.Provider

fun Project.collectModules(
    configurationName: String,
    variantName: String,
    interceptorModulesService: Provider<InterceptorModulesService>
) {
    val configProvider = try {
        configurations.named(configurationName)
    } catch (e: UnknownDomainObjectException) {
        println("Unable to find configuration $configurationName for variant $variantName.")
        interceptorModulesService.get().interceptorModules = emptyMap()
        interceptorModulesService.get().externalModules = emptyMap()
        return
    }

    configProvider.configure { configuration ->
        configuration.incoming.afterResolve {
            val allModules = it.resolutionResult.allComponents.versionMap()
            val interceptorModules = allModules.filter { (identifier, _) ->
                identifier.group == "com.sandymist.mobile"
            }.toMap()

            val externalModules = allModules.filter { (identifier, _) ->
                identifier.group != "com.sandymist.mobile"
            }.toMap()

            println("Detected interceptor modules $interceptorModules " +
                    "for variant: $variantName, config: $configurationName")
            interceptorModulesService.get().interceptorModules = interceptorModules
            interceptorModulesService.get().externalModules = externalModules
        }
    }
}

private fun Set<ResolvedComponentResult>.versionMap():
    List<Pair<ModuleIdentifier, SemVer>> {
    return mapNotNull {
        it.moduleVersion?.let { moduleVersion ->
            val identifier = moduleVersion.module
            val version = it.moduleVersion?.version ?: ""
            val semver = try {
                SemVer.parse(version)
            } catch (e: Throwable) {
                println("Unable to parse version $version of $identifier")
                SemVer()
            }
            return@mapNotNull Pair(identifier, semver)
        }
        null
    }
}
