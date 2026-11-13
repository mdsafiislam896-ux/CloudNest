package com.safiislam.nexvault.domain.model

data class CountryList(
    val countries: List<Country>,
    val detected: Country?
)
