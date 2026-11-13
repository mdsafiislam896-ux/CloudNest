package com.safiislam.nexvault.presentation

import com.safiislam.nexvault.presentation.di.sharedUiModule
import com.safiislam.nexvault.presentation.platform.AndroidPlatformCapabilities
import com.safiislam.nexvault.presentation.platform.AndroidStandardFolderPaths
import com.safiislam.nexvault.presentation.platform.PlatformCapabilities
import com.safiislam.nexvault.presentation.platform.StandardFolderPaths
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val presentationModule = module {
    singleOf(::AndroidStandardFolderPaths) bind StandardFolderPaths::class
    singleOf(::AndroidPlatformCapabilities) bind PlatformCapabilities::class
    includes(sharedUiModule)
}
