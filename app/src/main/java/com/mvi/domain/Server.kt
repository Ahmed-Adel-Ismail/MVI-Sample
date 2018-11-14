package com.mvi.domain

import com.mvi.entities.Gender
import com.mvi.entities.User
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.lang.IllegalArgumentException


fun login(username: String?, password: String?): Observable<User> {
    return if (isValidUsernameAndPassword(username, password)) {
        simulateLoginRequestFromServer(username, password)
    } else {
        Observable.fromCallable { throw IllegalArgumentException("invalid username or password") }
    }

}

fun simulateLoginRequestFromServer(username: String?, password: String?): Observable<User> {
    val delayMillis = 2000L
    Thread.sleep(delayMillis)
    return Observable.just(User(username!!, 20, Gender.MALE))
}
