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
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * A simple [Fragment] subclass.
 *
 */
class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LiteCycle.with(integration())
            .forLifeCycle(this)
            .onDestroyInvoke(Disposable::dispose)
            .observe()
    }

    private fun integration() = LoginViewState()
        .let(::Initialize)
        .let { BehaviorSubject.createDefault<LoginAction>(it) }
        .let { IntentData(it) }
        .let { intent(it).subscribe { state -> view(it.actions, state) } }

}

fun intent(data: IntentData): Observable<LoginViewState> = data.actions
    .subscribeOn(data.backgroundScheduler)
    .observeOn(data.backgroundScheduler)
    .switchMap { handleIntent(it, data.loginRequest) }
    .observeOn(data.mainScheduler)!!


private fun handleIntent(action: LoginAction, loginRequest: (String?, String?) -> Observable<User>) =
    when (action) {
        is Initialize -> Observable.just(LoginViewState(initialize = true))
        is LoginRequest -> processLoginRequest(loginRequest, action)

    }

private fun processLoginRequest(loginRequest: (String?, String?) -> Observable<User>, action: LoginRequest) =
    loginRequest(action.state.userName, action.state.password)
        .map { user -> LoginViewState(loginResponse = user) }
        .onErrorReturn { throwable -> LoginViewState(errorMessage = throwable.message) }


fun LoginFragment.view(actions: BehaviorSubject<LoginAction>, viewState: LoginViewState) = with(viewState) {
    login_progress.visibility = if (progressing) View.VISIBLE else View.INVISIBLE
    when {
        initialize -> login_button.setOnClickListener { handleOnClick(this, actions) }
        errorMessage != null -> Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        loginResponse != null -> findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }
}

private fun LoginFragment.handleOnClick(state: LoginViewState, actions: BehaviorSubject<LoginAction>) = with(state) {
    when {
        progressing -> Toast.makeText(context, "please wait", Toast.LENGTH_SHORT).show()
        loginResponse == null -> startLoginRequest(actions)
        else -> Toast.makeText(context, "login success", Toast.LENGTH_SHORT).show()
    }
}

private fun LoginFragment.startLoginRequest(actions: BehaviorSubject<LoginAction>) {
    login_progress.visibility = View.VISIBLE
    LoginViewState(userName = user_name_edit_text.asString(), password = password_edit_text.asString())
        .let(::LoginRequest)
        .also(actions::onNext)
}


data class IntentData(
    val actions: BehaviorSubject<LoginAction>,
    val loginRequest: ((String?, String?) -> Observable<User>) = ::login,
    val backgroundScheduler: Scheduler = Schedulers.io(),
    val mainScheduler: Scheduler = AndroidSchedulers.mainThread()
)

sealed class LoginAction
data class Initialize(val state: LoginViewState) : LoginAction()
data class LoginRequest(val state: LoginViewState) : LoginAction()

data class LoginViewState(
    val initialize: Boolean = false,
    val progressing: Boolean = false,
    val userName: String? = null,
    val password: String? = null,
    val errorMessage: String? = null,
    val loginResponse: User? = null
)








