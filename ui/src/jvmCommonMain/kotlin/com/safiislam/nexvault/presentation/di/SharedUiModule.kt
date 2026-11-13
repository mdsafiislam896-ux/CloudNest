package com.safiislam.nexvault.presentation.di

import com.safiislam.nexvault.presentation.cleaner.CleanerViewModel
import com.safiislam.nexvault.presentation.sync.SyncViewModel
import com.safiislam.nexvault.presentation.accounts.AccountsViewModel
import com.safiislam.nexvault.presentation.AppViewModel
import com.safiislam.nexvault.presentation.auth.AuthViewModel
import com.safiislam.nexvault.presentation.channels.ChannelsViewModel
import com.safiislam.nexvault.presentation.cloud.CloudHubViewModel
import com.safiislam.nexvault.presentation.devices.DevicesViewModel
import com.safiislam.nexvault.presentation.health.CloudHealthViewModel
import com.safiislam.nexvault.presentation.collection.CollectionViewModel
import com.safiislam.nexvault.presentation.files.FilesViewModel
import com.safiislam.nexvault.presentation.gallery.GalleryViewModel
import com.safiislam.nexvault.presentation.home.HomeViewModel
import com.safiislam.nexvault.presentation.onboarding.OnboardingViewModel
import com.safiislam.nexvault.presentation.note.NoteEditorViewModel
import com.safiislam.nexvault.presentation.preview.PreviewContentResolver
import com.safiislam.nexvault.presentation.preview.PreviewViewModel
import com.safiislam.nexvault.presentation.proxy.ProxyViewModel
import com.safiislam.nexvault.presentation.search.SearchViewModel
import com.safiislam.nexvault.presentation.settings.ExclusionsViewModel
import com.safiislam.nexvault.presentation.settings.SettingsViewModel
import com.safiislam.nexvault.presentation.transfers.TransfersViewModel
import com.safiislam.nexvault.presentation.trash.TrashViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedUiModule = module {
    singleOf(::PreviewContentResolver)
    viewModelOf(::AppViewModel)
    viewModelOf(::PreviewViewModel)
    viewModelOf(::ChannelsViewModel)
    viewModelOf(::CollectionViewModel)
    viewModelOf(::FilesViewModel)
    viewModelOf(::GalleryViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::NoteEditorViewModel)
    viewModelOf(::ProxyViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ExclusionsViewModel)
    viewModelOf(::TransfersViewModel)
    viewModelOf(::TrashViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::AccountsViewModel)
    viewModelOf(::SyncViewModel)
    viewModelOf(::CleanerViewModel)
    viewModelOf(::CloudHubViewModel)
    viewModelOf(::DevicesViewModel)
    viewModelOf(::CloudHealthViewModel)
}
