package com.narave05.ratetest.data.api.common

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class HeaderInterceptor(private val headersCallback: HeadersCallback) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            headersCallback().forEach { (k, v) ->
                addHeader(k, v)
            }
        }.build()
        return chain.proceed(request)
    }
}
