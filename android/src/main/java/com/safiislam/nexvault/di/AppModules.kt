package com.safiislam.nexvault.di

import com.safiislam.nexvault.core.cloud.cloudModule
import com.safiislam.nexvault.core.common.commonModule
import com.safiislam.nexvault.core.crypto.cryptoModule
import com.safiislam.nexvault.core.dispatchers.dispatchersModule
import com.safiislam.nexvault.core.files.filesModule
import com.safiislam.nexvault.core.files.sharedFilesModule
import com.safiislam.nexvault.core.logging.debugLogModule
import com.safiislam.nexvault.core.media.mediaModule
import com.safiislam.nexvault.core.network.networkModule
import com.safiislam.nexvault.core.power.powerModule
import com.safiislam.nexvault.core.permissions.permissionsModule
import com.safiislam.nexvault.core.proxy.proxyModule
import com.safiislam.nexvault.core.publish.publishModule
import com.safiislam.nexvault.core.security.securityModule
import com.safiislam.nexvault.core.telegram.telegramModule
import com.safiislam.nexvault.core.transfer.transferEngineModule
import com.safiislam.nexvault.core.transfer.transferModule
import com.safiislam.nexvault.core.update.updateModule
import com.safiislam.nexvault.data.local.database.daosModule
import com.safiislam.nexvault.data.local.database.databaseModule
import com.safiislam.nexvault.data.local.preferences.preferencesModule
import com.safiislam.nexvault.data.repository.localDataModule
import com.safiislam.nexvault.data.repository.repositoryModule
import com.safiislam.nexvault.domain.usecase.useCaseModule
import com.safiislam.nexvault.presentation.presentationModule

val appModules = listOf(
    cloudModule,
    commonModule,
    cryptoModule,
    dispatchersModule,
    filesModule,
    mediaModule,
    networkModule,
    powerModule,
    permissionsModule,
    proxyModule,
    publishModule,
    securityModule,
    telegramModule,
    transferModule,
    updateModule,
    databaseModule,
    daosModule,
    preferencesModule,
    repositoryModule,
    localDataModule,
    sharedFilesModule,
    debugLogModule,
    transferEngineModule,
    useCaseModule,
    presentationModule
)
