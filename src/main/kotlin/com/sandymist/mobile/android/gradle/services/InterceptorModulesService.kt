@file:Suppress("UnstableApiUsage") // Shared build services are incubating but available from 6.1

package com.sandymist.mobile.android.gradle.services

import com.sandymist.mobile.android.gradle.util.SemVer
import com.sandymist.mobile.android.gradle.util.getBuildServiceName
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class InterceptorModulesService : BuildService<BuildServiceParameters.None> {

    @get:Synchronized
    @set:Synchronized
    var interceptorModules: Map<ModuleIdentifier, SemVer> = emptyMap()

    @get:Synchronized
    @set:Synchronized
    var externalModules: Map<ModuleIdentifier, SemVer> = emptyMap()

    companion object {
        fun register(project: Project): Provider<InterceptorModulesService> {
            return project.gradle.sharedServices.registerIfAbsent(
                getBuildServiceName(InterceptorModulesService::class.java),
                InterceptorModulesService::class.java
            ) {}
        }
    }
}
