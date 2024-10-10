package com.example.kkbimage.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.kkbimage.App
import com.example.kkbimage.ui.main.model.MyFavoriteItem
import com.example.kkbimage.ui.main.model.SharedPref
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel() {
    var items = MutableLiveData<ArrayList<MyFavoriteItem>>(ArrayList())

    fun getItem(position: Int): MyFavoriteItem? {
        return items.value?.get(position)
    }

    fun getItemCount(): Int {
        items.value?.let {
            return items.value!!.size
        }?: run {
            return 0
        }
    }

    fun observeItems(owner: LifecycleOwner, observer: Observer<ArrayList<MyFavoriteItem>>){
        items.observe(owner, observer)
    }

    fun clickItem(item: MyFavoriteItem){
        try{
            var strUrl = item.thumbnail.split("/")
            if(strUrl.isNotEmpty()) {
                val hash = strUrl[strUrl.size - 1]
                viewModelScope.launch{
                    item.searchItem?.let {
                        item.searchItem!!.setHeartVisibility(false)
                    } ?: run {
                        SharedPref.deleteItem(hash)
                    }
                }
            }
        }catch (t: Throwable){
            Log.d(App.TAG, "clickItem Throwable: "+t.message)
        }
    }
    override fun onCleared() {
        super.onCleared()
        items.value?.clear()
    }
}