package com.safiislam.nexvault.data.repository

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val localDataModule = module {
    singleOf(::AndroidLocalDataWiper) bind LocalDataWiper::class
}
