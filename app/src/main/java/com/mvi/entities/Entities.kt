package com.mvi.entities

import java.io.Serializable

enum class Gender { MALE, FEMALE }
data class User(val name: String, val age: Int, val gender: Gender) : Serializable