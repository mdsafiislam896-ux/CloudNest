package com.safiislam.nexvault.core.common

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    singleOf(::AppNotifications)
}
