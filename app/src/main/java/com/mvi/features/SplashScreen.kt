package com.mvi.features


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.LiteCycle
import com.mvi.R
import io.reactivex.Completable
import io.reactivex.Observable
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
        LiteCycle.with(false)
            .forLifeCycle(this)
            .onStartUpdate { true }
            .onStopUpdate { false }
            .observe(BehaviorSubject.create())
            .let(::intent)
            .subscribe { view(it) }
            .disposeWithLifecycle(this)
    }


}


fun intent(canFinish: Observable<Boolean>): Observable<Boolean> {
    return canFinish.switchMap {
        return@switchMap if (it) {
            Observable.just(true).delay(2, TimeUnit.SECONDS)
        } else {
            Observable.just(false)
        }
    }
}


fun SplashFragment.view(finished: Boolean) {
    if (finished) {
        with(findNavController()){
            navigate(R.id.action_splashFragment_to_loginFragment)
        }

    }
}
