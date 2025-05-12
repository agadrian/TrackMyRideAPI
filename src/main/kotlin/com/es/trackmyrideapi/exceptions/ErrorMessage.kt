package com.es.trackmyrideapi.exceptions

data class ErrorMessage(
    val status: Int,
    val message: String,
    val path: String
)