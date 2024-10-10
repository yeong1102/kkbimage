package com.example.kkbimage.ui.main.model

class MyFavoriteItem(val createTime: Long, val thumbnail: String) : Comparable<MyFavoriteItem>{

    //var _heartVisibility: MutableLiveData<Boolean>? = null
    var searchItem: SearchItem? = null

    override fun compareTo(other: MyFavoriteItem): Int {
        try{
            return createTime.compareTo(other.createTime)
        }catch (t: Throwable){ }
        return 0
    }

}