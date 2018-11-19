package com.mvi.presentation.features.login

import io.reactivex.Observable

fun intent(data: IntentData) = with(data) {
    actions.share()
        .observeOn(backgroundScheduler)
        .subscribe { handleIntent(it, this@with) }
        .also { disposables.add(it) }
        .let { states }
}


private fun handleIntent(action: LoginAction, data: IntentData) {
    when (action) {
        is LoginInitializeAction -> Observable.just(LoginInProgressState(false))
        is LoginRequestAction -> processLoginRequest(data, action)
    }
}

private fun processLoginRequest(data: IntentData, action: LoginRequestAction) = with(data) {
    states.onNext(LoginInProgressState(true))
    loginRequest(action.username, action.password)
        .subscribeOn(backgroundScheduler)
        .observeOn(backgroundScheduler)
        .map { user -> LoginSuccessState(user) as LoginState }
        .onErrorReturn { throwable -> LoginFailureState(throwable.message ?: "failed to login") }
        .doOnNext { states.onNext(LoginInProgressState(false)) }
        .subscribe(states::onNext)
        .also { disposables.add(it) }

}