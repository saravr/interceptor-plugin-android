package com.sandymist.mobile.android.gradle.extensions

import javax.inject.Inject
import org.gradle.api.Project

abstract class InterceptorPluginExtension @Inject constructor(project: Project) {

    private val objects = project.objects

    val tracingInstrumentation: TracingInstrumentationExtension = objects.newInstance(
        TracingInstrumentationExtension::class.java
    )
}
