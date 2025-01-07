package com.example.mobile.android.gradle.instrumentation.util

import com.example.mobile.android.gradle.instrumentation.MethodContext
import org.objectweb.asm.MethodVisitor

interface ExceptionHandler {
    fun handle(exception: Throwable)
}

class CatchingMethodVisitor(
    apiVersion: Int,
    prevVisitor: MethodVisitor,
    private val className: String,
    private val methodContext: MethodContext,
    private val exceptionHandler: ExceptionHandler? = null
) : MethodVisitor(apiVersion, prevVisitor) {

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        try {
            super.visitMaxs(maxStack, maxLocals)
        } catch (e: Throwable) {
            exceptionHandler?.handle(e)
            println(
                """
                Error while instrumenting $className.${methodContext.name} ${methodContext.descriptor}.
                Please report this issue at https://github.com/getsentry/sentry-android-gradle-plugin/issues
                """.trimIndent()
            )
            throw e
        }
    }
}
