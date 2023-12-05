package com.mathroda.debitcardnfcreader.model

internal data class ApplicationFileLocator(
    val sfi: Int,
    val firstRecord: Int,
    val lastRecord: Int,
    val isOfflineAuthentication: Boolean
)
