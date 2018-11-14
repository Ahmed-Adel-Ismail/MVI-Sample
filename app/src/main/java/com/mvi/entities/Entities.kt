package com.mvi.entities

enum class Gender { MALE, FEMALE }
data class User(val name: String, val age: Int, val gender: Gender)