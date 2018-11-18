package com.mvi.presentation.features


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.LiteCycle
import com.mvi.R
import com.mvi.domain.login
import com.mvi.entities.User
import com.mvi.presentation.asString
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LiteCycle.with(integration())
            .forLifeCycle(this)
            .onDestroyInvoke(CompositeDisposable::clear)
            .observe()
    }

    private fun integration() = with(IntentData()) {
        intent(this)
            .subscribe { viewState -> view(actions, viewState) }
            .also { disposable -> disposables.add(disposable) }
            .let { disposables }
    }
}

fun intent(data: IntentData) = with(data) {
    BehaviorSubject.createDefault(LoginViewState()).apply {
        actions
            .share()
            .subscribe { handleIntent(it, this@with) }
            .also { disposables.add(it) }
    }
}


private fun handleIntent(action: LoginAction, data: IntentData) {
    when (action) {
        is Initialize -> Observable.just(LoginViewState())
        is LoginRequest -> processLoginRequest(data, action)

    }
}

private fun processLoginRequest(data: IntentData, action: LoginRequest) = with(data) {
    viewStates.onNext(LoginViewState(progressing = true))
    loginRequest(action.username, action.password)
        .subscribeOn(backgroundScheduler)
        .observeOn(backgroundScheduler)
        .doFinally { viewStates.onNext(LoginViewState(progressing = false)) }
        .map { user -> LoginViewState(loginResponse = user) }
        .onErrorReturn { throwable -> LoginViewState(errorMessage = throwable.message) }
        .observeOn(mainScheduler)
        .subscribe(viewStates::onNext, viewStates::onError)
        .also { data.disposables.add(it) }

}

fun LoginFragment.view(actions: BehaviorSubject<LoginAction>, viewState: LoginViewState) = with(viewState) {
    when {
        progressing -> showLoadingView()
        errorMessage != null -> showErrorView(this)
        loginResponse != null -> navigateToNextScreen()
        else -> showInitialView(actions)
    }
}

fun LoginFragment.showInitialView(actions: BehaviorSubject<LoginAction>) {
    login_progress.visibility = View.GONE
    login_button.setOnClickListener {
        actions.onNext(
            LoginRequest(
                user_name_edit_text.asString(),
                password_edit_text.asString()
            )
        )
    }
}

private fun LoginFragment.showLoadingView() {
    login_progress.visibility = View.VISIBLE
    login_button.setOnClickListener { Toast.makeText(context, "please wait", Toast.LENGTH_SHORT).show() }
}

private fun LoginFragment.showErrorView(loginViewState: LoginViewState) = with(loginViewState) {
    login_progress.visibility = View.GONE
    login_button.setOnClickListener { Toast.makeText(context, loginViewState.errorMessage, Toast.LENGTH_LONG).show() }
}

private fun LoginFragment.navigateToNextScreen() {
    login_progress.visibility = View.GONE
    login_button.setOnClickListener(null)
    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
}


data class IntentData(
    val actions: BehaviorSubject<LoginAction> = BehaviorSubject.createDefault(Initialize()),
    val viewStates: BehaviorSubject<LoginViewState> = BehaviorSubject.createDefault(LoginViewState()),
    val disposables: CompositeDisposable = CompositeDisposable(),
    val loginRequest: ((String?, String?) -> Observable<User>) = ::login,
    val backgroundScheduler: Scheduler = Schedulers.io(),
    val mainScheduler: Scheduler = AndroidSchedulers.mainThread()
)


sealed class LoginAction
data class Initialize(val progressing: Boolean = false) : LoginAction()
data class LoginRequest(val username: String?, val password: String?) : LoginAction()

data class LoginViewState(
    val progressing: Boolean = false,
    val errorMessage: String? = null,
    val loginResponse: User? = null
)








