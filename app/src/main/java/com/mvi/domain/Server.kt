package com.mvi.domain

import com.mvi.entities.Gender
import com.mvi.entities.User
import java.lang.IllegalArgumentException


fun login(username: String, password: String): User {
    val delayMillis = 2000L
    Thread.sleep(delayMillis)
    if (!username.isBlank() && !password.isBlank()) {
        return User(username, 20, Gender.MALE)
    } else {
        throw IllegalArgumentException("invalid username or password")
    }
}