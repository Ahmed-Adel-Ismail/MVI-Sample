package com.mvi.presentation.features


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.LiteCycle
import com.mvi.R
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 *
 */
class SplashFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LiteCycle.with(integration())
            .forLifeCycle(this)
            .onDestroyInvoke(Disposable::dispose)
            .observe()
    }

    private fun integration() = intent(fragmentStartedObservable()).subscribe { view(it) }

    private fun fragmentStartedObservable() = LiteCycle.with(false)
        .forLifeCycle(this)
        .onStartUpdate { true }
        .onStopUpdate { false }
        .observe(BehaviorSubject.create())

}


fun intent(fragmentStartedObservable: Observable<Boolean>) = fragmentStartedObservable
    .switchMap { Observable.just(it).delay(2, TimeUnit.SECONDS) }!!


fun SplashFragment.view(finished: Boolean) = findNavController()
    .takeIf { finished }
    ?.apply { navigate(R.id.action_splashFragment_to_loginFragment) }


