package com.narave05.ratetest.di

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier

inline fun <reified T> KoinComponent.injectByParams(
    vararg params: Any,
    qualifier: Qualifier? = null
): Lazy<T> = inject(qualifier = qualifier, parameters = { parametersOf(params) })
