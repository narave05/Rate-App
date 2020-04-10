package com.narave05.ratetest.data.api.common

import com.narave05.ratetest.di.RetrofitQualifier
import com.narave05.ratetest.di.apiClientModule
import com.narave05.ratetest.di.injectByParams
import org.koin.core.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.inject
import retrofit2.Retrofit

typealias HeadersCallback = () -> Map<String, String>

data class ApiServiceConfig(
    val baseUrl: String,
    val headersCallback: HeadersCallback = { mapOf() },
    val isDebugMode: Boolean
)

class ApiServiceCreator(apiServiceConfig: ApiServiceConfig) : KoinComponent {

    private val defaultRetrofit: Retrofit by inject(RetrofitQualifier.DEFAULT_INSTANCE)

    init {
        loadKoinModules(listOf(
            apiClientModule(
                apiServiceConfig
            )
        ))
    }

    inline fun <reified API_SERVICE_INTERFACE> create(
        clientConfigType: ClientConfigType = DefaultConfigType
    ): API_SERVICE_INTERFACE {
        return getRetrofitByType(clientConfigType).create(API_SERVICE_INTERFACE::class.java)
    }

    fun getRetrofitByType(type: ClientConfigType) = when (type) {
        DefaultConfigType -> defaultRetrofit
        is CachedConfigType -> {
            val cachedRetrofit: Retrofit
                    by injectByParams(
                        type.cachedFile,
                        qualifier = RetrofitQualifier.CACHED_INSTANCE
                    )
            cachedRetrofit
        }
    }

}
