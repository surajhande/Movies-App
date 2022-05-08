package com.suraj.moviesapp.ui

import android.widget.ImageView
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions


fun ImageView.loadFromUrl(url: String) {
    if (url.isBlank()) return
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

interface UiModel

open class TransientAwareUiModel(var isRedelivered: Boolean = false) : UiModel

class TransientAwareLiveData<T : TransientAwareUiModel> : MutableLiveData<T>() {
    private var previousValue: T? = null

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, Observer {
            value?.isRedelivered = false
            var properState =
                (owner.lifecycle.currentState == Lifecycle.State.CREATED
                        || owner.lifecycle.currentState == Lifecycle.State.STARTED)
            if (properState && value == previousValue) {
                value?.isRedelivered = true
            }
            observer.onChanged(value)
            previousValue = value
        })


    }
}
