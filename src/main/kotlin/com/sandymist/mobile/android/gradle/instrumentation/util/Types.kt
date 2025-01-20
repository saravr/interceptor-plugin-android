package com.sandymist.mobile.android.gradle.instrumentation.util

import org.objectweb.asm.Type

object Types {
    // COMMON
    val OBJECT = Type.getType("Ljava/lang/Object;")
    val ITERABLE = Type.getType("Ljava/lang/Iterable;")
    val ITERATOR = Type.getType("Ljava/util/Iterator;")
    val COLLECTION = Type.getType("Ljava/util/Collection;")

    // OKHTTP
    val OKHTTP_INTERCEPTOR = Type.getType("Lokhttp3/Interceptor;")
//    val INSTRUMENTED_OKHTTP_INTERCEPTOR =
//        Type.getType("Lcom/sandymist/mobile/plugins/network/NetworkPlugin;")
}
