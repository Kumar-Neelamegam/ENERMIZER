package com.peter.enermizer.data

data class RaspberryPiRelayDataset(
    val status: Boolean?,
    val error: String?,
    val message: String?,
    val relay: Int?,
)
