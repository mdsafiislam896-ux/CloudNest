package com.safiislam.nexvault.core.power

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val powerModule = module {
    singleOf(::AndroidPowerMonitor) bind PowerMonitor::class
}
