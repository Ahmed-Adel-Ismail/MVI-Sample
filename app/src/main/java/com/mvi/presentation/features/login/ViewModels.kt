package com.mvi.presentation.features.login

import com.mvi.domain.login
import com.mvi.entities.User
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

data class IntentData(
    val actions: BehaviorSubject<LoginAction> = BehaviorSubject.createDefault(LoginInitializeAction()),
    val states: BehaviorSubject<LoginState> = BehaviorSubject.createDefault(LoginInProgressState(false)),
    val disposables: CompositeDisposable = CompositeDisposable(),
    val loginRequest: ((String?, String?) -> Observable<User>) = ::login,
    val backgroundScheduler: Scheduler = Schedulers.io(),
    val mainScheduler: Scheduler = AndroidSchedulers.mainThread()
)


sealed class LoginAction
data class LoginInitializeAction(val progressing: Boolean = false) : LoginAction()
data class LoginRequestAction(val username: String?, val password: String?) : LoginAction()

sealed class LoginState(open val progressing: Boolean, open val errorMessage: String?, open val loginResponse: User?)
data class LoginInProgressState(override val progressing: Boolean) : LoginState(progressing, null, null)
data class LoginSuccessState(override val loginResponse: User) : LoginState(false, null, loginResponse)
data class LoginFailureState(override val errorMessage: String) : LoginState(false, errorMessage, null)
