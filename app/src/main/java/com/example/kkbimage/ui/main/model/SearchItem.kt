package com.example.kkbimage.ui.main.model

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.kkbimage.App
import java.util.*
import androidx.databinding.library.baseAdapters.BR

class SearchItem(var title: String = "", var thumbnail: String = ""): BaseObservable(), Comparable<SearchItem>{
    lateinit var dateTime: Date
    var separator: Boolean = false
    private var _heartVisibility: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    constructor(title: String, sep: Boolean) : this(title,"") {
        separator = sep
    }

    override fun compareTo(other: SearchItem): Int {
        try{
            return dateTime.compareTo(other.dateTime)
        }catch (t: Throwable){ }
        return 0
    }

    fun observeHeartVisibility(owner: LifecycleOwner, v: Boolean, onChangedHeart: ((SearchItem) -> Unit)){
        if(v){
            _heartVisibility.value = v
        }
        _heartVisibility?.observe(owner) {
            Log.d(App.TAG, "_heartVisibility changed !!!!!!!!!! "+it)
            onChangedHeart(this)
        }
    }

    @Bindable
    fun getHeartVisibility(): Boolean {
        return _heartVisibility?.value == true
    }

    fun setHeartVisibility(value: Boolean) {
        _heartVisibility?.postValue(value)
        notifyPropertyChanged(BR.heartVisibility)
    }
}