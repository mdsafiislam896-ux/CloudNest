package com.safiislam.nexvault.presentation.common

sealed interface AgeBucket {
    data object JustNow : AgeBucket
    data class Minutes(val value: Int) : AgeBucket
    data class Hours(val value: Int) : AgeBucket
    data class Days(val value: Int) : AgeBucket
    data class Longer(val epochMillis: Long) : AgeBucket
}
