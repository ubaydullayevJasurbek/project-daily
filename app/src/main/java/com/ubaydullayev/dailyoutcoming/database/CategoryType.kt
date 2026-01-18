package com.ubaydullayev.dailyoutcoming.database

data class CategoryType(
    val id: Int,
    val title: String,
    val description: String,
    val value:Double = 1.00
){
    override fun toString(): String = title
}
