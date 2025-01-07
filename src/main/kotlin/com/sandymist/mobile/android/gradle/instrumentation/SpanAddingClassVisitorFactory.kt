package com.sandymist.mobile.android.gradle.instrumentation

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.sandymist.mobile.android.gradle.extensions.InstrumentationFeature
import com.sandymist.mobile.android.gradle.instrumentation.okhttp.OkHttp
import com.sandymist.mobile.android.gradle.instrumentation.util.findClassReader
import com.sandymist.mobile.android.gradle.instrumentation.util.findClassWriter
import com.sandymist.mobile.android.gradle.instrumentation.util.isMinifiedClass
import com.sandymist.mobile.android.gradle.services.InterceptorModulesService
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.objectweb.asm.ClassVisitor
import java.io.File

@Suppress("UnstableApiUsage")
abstract class SpanAddingClassVisitorFactory :
    AsmClassVisitorFactory<SpanAddingClassVisitorFactory.SpanAddingParameters> {

    interface SpanAddingParameters : InstrumentationParameters {

        /**
         * AGP will re-instrument dependencies, when the [InstrumentationParameters] changed
         * https://issuetracker.google.com/issues/190082518#comment4. This is just a dummy parameter
         * that is used solely for that purpose.
         */
        @get:Input
        @get:Optional
        val invalidate: Property<Long>

        @get:Input
        val debug: Property<Boolean>

        @get:Input
        val features: SetProperty<InstrumentationFeature>

        @get:Internal
        val interceptorModulesService: Property<InterceptorModulesService>

        @get:Internal
        val tmpDir: Property<File>

        @get:Internal
        var _instrumentable: ClassInstrumentable?
    }

    private val instrumentable: ClassInstrumentable
        get() {
            val memoized = parameters.get()._instrumentable
            if (memoized != null) {
                //println("Instrumentable: $memoized [Memoized]")
                return memoized
            }

            val interceptorModules = parameters.get().interceptorModulesService.get().interceptorModules

            //println("Read interceptor modules: $interceptorModules")
            val instrumentable = ChainedInstrumentable(
                listOfNotNull(
                    OkHttp()
                )
            )

            //println("Instrumentable: $instrumentable")
            parameters.get()._instrumentable = instrumentable
            return instrumentable
        }

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val className = classContext.currentClassData.className

        val classReader = nextClassVisitor.findClassWriter()?.findClassReader()
        val isMinifiedClass = classReader?.isMinifiedClass() ?: false
        if (isMinifiedClass) {
            println("$className skipped from instrumentation because it's a minified class.")
            return nextClassVisitor
        }

        return instrumentable.getVisitor(
            classContext,
            instrumentationContext.apiVersion.get(),
            nextClassVisitor,
            parameters = parameters.get()
        )
    }

    override fun isInstrumentable(classData: ClassData): Boolean =
        instrumentable.isInstrumentable(classData.toClassContext())
}
