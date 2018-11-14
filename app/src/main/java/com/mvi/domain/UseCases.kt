package com.mvi.domain


fun isValidUsernameAndPassword(username: String?, password: String?) =
    !username.isNullOrBlank() && !password.isNullOrBlank()
