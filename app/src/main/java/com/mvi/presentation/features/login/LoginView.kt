package com.mvi.presentation.features.login


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.android.LiteCycle
import com.mvi.R
import com.mvi.presentation.asString
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LiteCycle.with(cycleDisposables())
            .forLifeCycle(this)
            .onDestroyInvoke(CompositeDisposable::clear)
            .observe()
    }

    private fun cycleDisposables() =
        IntentData().apply { disposables.add(cycle(this)) }.disposables


    private fun cycle(data: IntentData) = with(data) {
        intent(this).observeOn(mainScheduler).subscribe { view(actions, it) }
    }

}


fun LoginFragment.view(actions: BehaviorSubject<LoginAction>, state: LoginState) = with(state) {
    when (this) {
        is LoginInProgressState -> switchProgressAndOnClick(actions, progressing)
        is LoginSuccessState -> navigateToNextScreen()
        is LoginFailureState -> showErrorViewAndEnableOnClick(errorMessage, actions)
    }
}

private fun LoginFragment.showErrorViewAndEnableOnClick(errorMessage: String, actions: BehaviorSubject<LoginAction>) {
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    login_button.setOnClickListener { requestLoginOnClick(actions) }
}


private fun LoginFragment.requestLoginOnClick(actions: BehaviorSubject<LoginAction>) {
    actions.onNext(LoginRequestAction(user_name_edit_text.asString(), password_edit_text.asString()))
}

private fun LoginFragment.switchProgressAndOnClick(actions: BehaviorSubject<LoginAction>, progressing: Boolean) {
    if (progressing) showProgressAndDisableOnClick() else hideProgressAndEnableOnClick(actions)
}

private fun LoginFragment.hideProgressAndEnableOnClick(actions: BehaviorSubject<LoginAction>) {
    login_progress.visibility = View.GONE
    login_button.setOnClickListener { requestLoginOnClick(actions) }
}

private fun LoginFragment.showProgressAndDisableOnClick() {
    login_progress.visibility = View.VISIBLE
    login_button.setOnClickListener { Toast.makeText(context, "please wait", Toast.LENGTH_SHORT).show() }
}


private fun LoginFragment.navigateToNextScreen() {
    login_button.setOnClickListener(null)
    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
}











