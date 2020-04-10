package com.narave05.ratetest.data.api.common

import java.io.File

sealed class ClientConfigType

object DefaultConfigType : ClientConfigType()
data class CachedConfigType(var cachedFile: File): ClientConfigType()
