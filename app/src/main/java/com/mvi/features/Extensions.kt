package com.mvi.features

import android.arch.lifecycle.LifecycleOwner
import com.android.LiteCycle
import io.reactivex.disposables.Disposable


fun Disposable.disposeWithLifecycle(lifecycleOwner: LifecycleOwner) =
    LiteCycle.with(this)
        .forLifeCycle(lifecycleOwner)
        .onDestroyInvoke(Disposable::dispose)
        .observe()
