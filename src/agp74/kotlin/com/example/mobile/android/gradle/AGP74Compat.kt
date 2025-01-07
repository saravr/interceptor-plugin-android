@file:Suppress("UnstableApiUsage")

package com.example.mobile.android.gradle

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant

fun <T : InstrumentationParameters> configureInstrumentationFor74(
    variant: Variant,
    classVisitorFactoryImplClass: Class<out AsmClassVisitorFactory<T>>,
    scope: InstrumentationScope,
    mode: FramesComputationMode,
    instrumentationParamsConfig: (T) -> Unit
) {
    variant.instrumentation.transformClassesWith(
        classVisitorFactoryImplClass,
        scope,
        instrumentationParamsConfig
    )
    variant.instrumentation.setAsmFramesComputationMode(mode)
}

fun onVariants74(
    androidComponentsExt: AndroidComponentsExtension<*, *, *>,
    callback: (Variant) -> Unit
) {
    androidComponentsExt.onVariants(callback = callback)
}
