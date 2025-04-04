package com.example.medipal.data.model

data class DoctorInfo(
    val id: String,
    val name: String,
    val specialization: String,
    val imageRes: Int,
    val experience: String,
    val rating: Float,
    val reviews: Int
) 