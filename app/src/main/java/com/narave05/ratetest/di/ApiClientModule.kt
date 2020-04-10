package com.narave05.ratetest.di

import com.narave05.ratetest.data.api.common.ApiServiceConfig
import com.narave05.ratetest.data.api.common.HeaderInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

internal data class OkHttpConfig(
    val connectionTimeout: Long = OkHttpProps.CONNECTION_TIMEOUT,
    val readAndWriteTimeout: Long = OkHttpProps.READ_WRITE_TIMEOUT,
    val interceptors: List<Interceptor> = listOf(),
    val cacheFile: File? = null
)

object RetrofitQualifier {
    val DEFAULT_INSTANCE = named("retrofit_default_instance")
    val CACHED_INSTANCE = named("retrofit_client_instance")
}

object OkHttpProps {
    const val CACHE_SIZE = 10 * 1024 * 1024L
    const val CONNECTION_TIMEOUT = 10L
    const val READ_WRITE_TIMEOUT = 10L
}

internal fun apiClientModule(apiServiceConfig: ApiServiceConfig) = module {

    single {
        Retrofit.Builder().apply {
            baseUrl(apiServiceConfig.baseUrl)
            addConverterFactory(GsonConverterFactory.create())
        }
    }

    factory(RetrofitQualifier.DEFAULT_INSTANCE) {
        val config = OkHttpConfig(interceptors = get())
        val defaultClient = configAndGetOkHttp(config).build()
        get<Retrofit.Builder>()
            .client(defaultClient)
            .build()
    }

    factory(RetrofitQualifier.CACHED_INSTANCE) { (file: File) ->
        val config = OkHttpConfig(
            interceptors = get(),
            cacheFile = file
        )
        val cachedClient = configAndGetOkHttp(config).build()
        get<Retrofit.Builder>()
            .client(cachedClient)
            .build()
    }

    factory {
        mutableListOf<Interceptor>().apply {
            if (apiServiceConfig.isDebugMode) {
                val loggingHeader = HttpLoggingInterceptor()
                loggingHeader.level = HttpLoggingInterceptor.Level.HEADERS
                add(loggingHeader)
                val loggingBody = HttpLoggingInterceptor()
                loggingBody.level = HttpLoggingInterceptor.Level.BODY
                add(loggingBody)
            }
            if (apiServiceConfig.headersCallback().isNotEmpty()) {
                add(
                    HeaderInterceptor(
                        apiServiceConfig.headersCallback
                    )
                )
            }
        }.toList()
    }

}

private fun configAndGetOkHttp(config: OkHttpConfig) = OkHttpClient.Builder().apply {
    connectTimeout(config.connectionTimeout, TimeUnit.SECONDS)
    readTimeout(config.readAndWriteTimeout, TimeUnit.SECONDS)
    writeTimeout(config.readAndWriteTimeout, TimeUnit.SECONDS)
    config.cacheFile?.let {
        cache(
            Cache(
                config.cacheFile,
                OkHttpProps.CACHE_SIZE
            )
        )
    }
    val interceptors = config.interceptors
    if (interceptors.isNotEmpty()) {
        interceptors.forEach { interceptor ->
            addInterceptor(interceptor)
        }
    }
}