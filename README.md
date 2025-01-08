# Interceptor Gradle Plugin for Android

This plugin can be used to provide an OkHTTP3 interceptor (may be as part of a library), and used by an Android app without explicitly adding it when building the OkHTTP client. All that is required is applying this Gradle plugin. This is particularly useful for interceptors intended for purposes such as debugging, which are not core components of the application.

# How to use

## Subclass okhttp3 `Interceptor`

```
package com.sandymist.mobile.plugins.network

import okhttp3.Interceptor
import okhttp3.Response

class NetworkPlugin: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // your logic here...
        return response
    }
}
```

Note: The package name will be customizable in a future release, but for now, it has to be as mentioned here.


## Apply the Gradle plugin in your app or library

```
plugins {
    id("com.sandymist.mobile.plugin.interceptor") version "<release>"
}
```

The interceptor will be applied automatically.

# Credits

This gradle plugin is adopted from [Sentry Android Gradle Plugin](https://github.com/getsentry/sentry-android-gradle-plugin) 
which provides interceptor functionality using ASM bytecode manipulation framework (same technology used by Embrace, for example.)
