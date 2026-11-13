package com.safiislam.nexvault.core.common

fun interface LogSink {
    fun write(level: Char, tag: String, message: String, throwable: Throwable?)
}
