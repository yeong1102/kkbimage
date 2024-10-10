package com.example.kkbimage.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.kkbimage.App
import com.example.kkbimage.ui.main.model.SharedPref
import com.example.kkbimage.ui.main.SearchAdapter
import com.example.kkbimage.ui.main.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class SearchViewModel @Inject constructor(
    val api: KakaoApi
): ViewModel() {

    protected val disposables = CompositeDisposable()
    private lateinit var owner: LifecycleOwner

    var image_page_count: Int = 0
    var video_page_count: Int = 0
    var is_image_end: Boolean = false
    var is_video_end: Boolean = false
    var searchWord: String = "Chat GPT"

    // Pair.first: response status
    // Pair.second: response isSuccessful
    val _image_responsed = MutableLiveData<Pair<Boolean, Boolean>>()
    val _video_responsed = MutableLiveData<Pair<Boolean, Boolean>>()

    var searchResult: ArrayList<SearchItem> = ArrayList<SearchItem>()
    var items: ArrayList<SearchItem> = ArrayList<SearchItem>()
    var loading = false

    var positionStart = 0
    var insertedItemCount = 0

    fun search(word: String) {
        searchWord = word
        if(loading)
            return
        loading = true
        requestSearchImage()
        requestSearchVideo()
    }

    fun searchWithScroll(adapter: RecyclerView.Adapter<SearchAdapter.ViewHolder>) {
        if(loading)
            return
        items.add(SearchItem(Math.max(image_page_count, video_page_count).toString(), true))
        adapter.notifyItemInserted(items.size-1)
        loading = true
        requestSearchImage()
        requestSearchVideo()
    }

    private fun requestSearchImage() = viewModelScope.launch{
        if(is_image_end)
            return@launch

        image_page_count++
        Log.d(App.TAG, "page_count: "+image_page_count)
        var response = api.searchImage(searchWord, image_page_count)
        if (response.isSuccessful) {
            Log.d(App.TAG, "response is Successful")
            val data = response.body()
            parsingImage(data)
        }
        else{
            Log.d(App.TAG, "response is fail")
            Log.d(App.TAG, "code is "+response.code())
            Log.d(App.TAG, "message is "+response.message())
            image_page_count--
            _image_responsed.postValue(Pair(true, false))
        }
    }


    private fun requestSearchVideo() = viewModelScope.launch{
        if(is_video_end)
            return@launch

        video_page_count++
        Log.d(App.TAG, "page_count: "+video_page_count)
        var response = api.searchVideo(searchWord, video_page_count)
        if (response.isSuccessful) {
            Log.d(App.TAG, "response is Successful")
            val data = response.body()
            parsingVideo(data)
        }
        else{
            Log.d(App.TAG, "response is fail")
            Log.d(App.TAG, "code is "+response.code())
            Log.d(App.TAG, "message is "+response.message())
            video_page_count--
            _video_responsed.postValue(Pair(true, false))
        }
    }

    fun getItem(position: Int): SearchItem {
        return items.get(position)
    }

    fun getItemCount(): Int {
        return items.size
    }

    private fun parsingImage(data: SearchImageDto?) {

        data?.let {
            Log.d(App.TAG, "page is is_end: "+it.meta.is_end)
            Log.d(App.TAG, "documents.size: "+it.documents.size)

            is_image_end = it.meta.is_end
            val list: Iterator<SearchImageData> = data.documents.listIterator()
            if(data.documents.size>0){
                Log.d(App.TAG, "item0: "+data.documents.get(0).thumbnail_url+" "+data.documents.get(0).datetime)
                insertedItemCount += data.documents.size
            }

            while (list.hasNext()) {
                val item = list.next()

                var datetimes = item.datetime?.split(".")
                var datetime = datetimes?.get(0)?.split("T")
                var ymd = datetime?.get(0)
                var time = datetime?.get(1)
                ymd?.let {
                    time?.let {
                        var title = String.format("%s %s", ymd, time)
                        addSearchResult(title, item.thumbnail_url, item.datetime)
                    }
                }
            }

            Log.d(App.TAG, ";;;;;;;;;;;")
        }
        _image_responsed.postValue(Pair(true, true))
    }

    private fun parsingVideo(data: SearchVideoDto?) {
        data?.let {
            Log.d(App.TAG, "page is is_end: "+it.meta.is_end)
            Log.d(App.TAG, "documents.size: "+it.documents.size)

            is_video_end = it.meta.is_end
            val list: Iterator<SearchVideoData> = data.documents.listIterator()
            if(data.documents.size>0){
                Log.d(App.TAG, "item0: "+data.documents.get(0).thumbnail+" "+data.documents.get(0).datetime)
                insertedItemCount += data.documents.size
            }

            while (list.hasNext()) {
                val item = list.next()

                var datetimes = item.datetime?.split(".")
                var datetime = datetimes?.get(0)?.split("T")
                var ymd = datetime?.get(0)
                var time = datetime?.get(1)
                ymd?.let {
                    time?.let {
                        var title = String.format("%s %s", ymd, time)
                        addSearchResult(title, item.thumbnail, item.datetime)
                    }
                }
            }

            Log.d(App.TAG, ";;;;;;;;;;;")
        }
        _video_responsed.postValue(Pair(true, true))
    }

    private fun addSearchResult(title: String, thumbnail: String, datetime: String){
        var searchItem = SearchItem(title, thumbnail)
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val date: Date = formatter.parse(datetime)
        searchItem.dateTime = date

        var strUrl = searchItem.thumbnail.split("/")
        if(strUrl.isNotEmpty()){
            val hash = strUrl[strUrl.size-1]
            if(SharedPref.dataFavoriteMap.containsKey(hash)){
                var MyFavoriteItem = SharedPref.dataFavoriteMap.get(hash)
                MyFavoriteItem?.searchItem = searchItem
                searchItem.observeHeartVisibility(owner, true, onChangedHeart)
            }else{
                searchItem.observeHeartVisibility(owner, false, onChangedHeart)
            }
        }

        searchResult.add(searchItem)
    }

    fun observeResponse(viewLifecycleOwner: LifecycleOwner, adapter: RecyclerView.Adapter<SearchAdapter.ViewHolder>){
        Log.d(App.TAG, "observeResponse S=>")
        owner = viewLifecycleOwner
        _image_responsed.observe(viewLifecycleOwner, Observer {
            Log.d(App.TAG, "image_responsed changed !!!!!!!!!!")
            notifyDataSetChanged(adapter)
        })
        _video_responsed.observe(viewLifecycleOwner, Observer {
            Log.d(App.TAG, "video_responsed changed !!!!!!!!!!")
            notifyDataSetChanged(adapter)
        })
    }

    fun notifyDataSetChanged(adapter: RecyclerView.Adapter<SearchAdapter.ViewHolder>) = viewModelScope.launch{
        _image_responsed.value?.let {
            _video_responsed.value?.let {
                if(_image_responsed.value!!.first && _video_responsed.value!!.first){

                    //if(_image_responsed.value!!.second && _video_responsed.value!!.second){
                        Log.d(App.TAG, "Collections.sort & adapter.notify S=>")

                        if(searchResult.size>0) {

                            Collections.sort(searchResult, Collections.reverseOrder ())
                            items.addAll(searchResult)

                            adapter.notifyDataSetChanged()
                            Log.d(App.TAG, "positionStart: "+positionStart)
                            Log.d(App.TAG, "insertedItemCount: "+insertedItemCount)
                            for(i in positionStart until items.size){

                            }
                            positionStart+=insertedItemCount
                            Log.d(App.TAG, ";;;;;;;;;;;")
                            Log.d(App.TAG, ";;;;;;;;;;;")
                            Log.d(App.TAG, ";;;;;;;;;;;")
                        }

                        loading = false
                        resetResponse()
                        Log.d(App.TAG, "Collections.sort & adapter.notify <=E")
                    //}
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        resetSearchResult(null)
    }

    private fun resetResponse(){
        _image_responsed.postValue(Pair(false, true))
        _video_responsed.postValue(Pair(false, true))
        searchResult.clear()
        insertedItemCount = 0
    }

    fun resetSearchResult(adapter: RecyclerView.Adapter<SearchAdapter.ViewHolder>?){
        searchWord = ""
        image_page_count = 0
        video_page_count = 0
        is_image_end = false
        is_video_end = false
        items.clear()
        adapter?.notifyDataSetChanged()
    }

    var onChangedHeart: ((SearchItem) -> Unit) = {
        var strUrl = it.thumbnail.split("/")
        if(strUrl.isNotEmpty()){
            val visibility = it.getHeartVisibility()
            val hash = strUrl[strUrl.size-1]
            Log.d(App.TAG, "onChangedHeart :: "+hash+" "+visibility)

            if (visibility != null) {
                viewModelScope.launch{
                    if(visibility){
                        SharedPref.insertItem(hash, it)
                    }
                    else{
                        SharedPref.deleteItem(hash)
                    }
                }
            }
        }
    }
}