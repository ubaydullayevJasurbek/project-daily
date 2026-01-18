package com.ubaydullayev.dailyoutcoming.model

import java.io.Serializable

data class ValutaModel(
    val id: Int,
    val currency: String,
    val flag: Int,
    var amount: Double
): Serializable