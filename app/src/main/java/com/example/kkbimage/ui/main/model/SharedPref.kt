package com.example.kkbimage.ui.main.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.kkbimage.App
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SharedPref {

    companion object{
        val PREFERENCES_NAME = "PREFERENCES_NAME"
        //val HASHSET_NAME = "hashSetName"
        val HASHLIST_NAME = "hashList"
        val OBJECT_NAME_TIME = "createTime"
        val OBJECT_NAME_THUMBNAIL = "thumbnail"

        lateinit var prefs: SharedPreferences
        var hashList: ArrayList<String> = ArrayList()
        var dataFavoriteMap: HashMap<String, MyFavoriteItem> = HashMap()
        var dataFavorite: ArrayList<MyFavoriteItem> = ArrayList()
        lateinit var favoriteLiveData: MutableLiveData<ArrayList<MyFavoriteItem>>
        val mutex = Mutex()
        var dataSetReady = false

        fun getPreferences(context: Context): SharedPreferences? {
            return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        }

        suspend fun insertItem(hash: String, searchItem: SearchItem) = runBlocking{
            mutex.withLock {
                insertData(hash, searchItem)
            }
        }

        private fun insertData(hash: String, searchItem: SearchItem){
            try{
                Log.d(App.TAG, "insertData S ===> ")
                if(!dataFavoriteMap.contains(hash)) {
                    val now = System.currentTimeMillis()
                    var jsonObject = JSONObject()
                    jsonObject.put(OBJECT_NAME_TIME, now)
                    jsonObject.put(OBJECT_NAME_THUMBNAIL, searchItem.thumbnail)

                    var item = MyFavoriteItem(now, searchItem.thumbnail)
                    item.searchItem = searchItem
                    dataFavoriteMap.put(hash, item)
                    dataFavorite.add(item)
                    hashList.add(hash)

                    val editor = prefs.edit()
                    editor?.putString(hash, jsonObject.toString())?.commit()
                    editor?.putString(HASHLIST_NAME, hashList.toString())?.commit()

                    Log.d(App.TAG, "data: "+jsonObject.toString())
                    Log.d(App.TAG, "hashList: "+hashList.toString())

                    favoriteLiveData.postValue(dataFavorite)
                }
                Log.d(App.TAG, "insertData < === E")
            }catch (t: Throwable){}
        }

        suspend fun deleteItem(hash: String) = runBlocking {
            mutex.withLock {
                deleteData(hash)
            }
        }

        private fun deleteData(hash: String) {
            try{
                if(dataFavoriteMap.contains(hash)) {
                    var item = dataFavoriteMap.get(hash)

                    dataFavoriteMap.remove(hash)
                    dataFavorite.remove(item)
                    hashList.remove(hash)

                    val editor = prefs.edit()
                    editor?.remove(hash)?.commit()
                    editor?.putString(HASHLIST_NAME, hashList.toString())?.commit()

                    favoriteLiveData.postValue(dataFavorite)
                }
            }catch (t: Throwable){}
        }

        suspend fun loadData(context: Context, items: MutableLiveData<ArrayList<MyFavoriteItem>>){
            try{
                hashList.clear()
                dataFavoriteMap.clear()
                dataFavorite.clear()

                favoriteLiveData = items
                Log.d(App.TAG, "loadData S ===> dataArray.size: "+ dataFavorite.size)
                prefs = getPreferences(context)!!
                val hashs = prefs?.getString(HASHLIST_NAME, null)
                Log.d(App.TAG, "str: "+hashs)

                hashs?.let {
                    val jsonArr = JSONArray(hashs)
                    Log.d(App.TAG, "jsonArr.length(): "+jsonArr.length())

                    for (i in 0 until jsonArr.length()){
                        val hash: String = jsonArr.getString(i)
                        //Log.d(App.TAG, "hash: "+hash)
                        val strJson = prefs?.getString(hash, "")
                        if(!strJson.equals("")){
                            val jsonObject = JSONObject(strJson)
                            val createTime = jsonObject.getLong(OBJECT_NAME_TIME)
                            val thumbnail = jsonObject.getString(OBJECT_NAME_THUMBNAIL)

                            var item = MyFavoriteItem(createTime, thumbnail)
                            dataFavoriteMap.put(hash, item)
                            dataFavorite.add(item)
                            hashList.add(hash)
                            //Log.d(App.TAG, "jsonArray.get("+"): "+hash+" time: "+createTime+" thumb: "+thumbnail)
                        }
                    }
                    Collections.sort(dataFavorite)
                    Log.d(App.TAG, "loadData < ===E dataArray.size: "+ dataFavorite.size)
                    favoriteLiveData.postValue(dataFavorite)
                }
            }catch (t: Throwable){
                Log.d(App.TAG, ""+t.message+" "+t.stackTraceToString())
            }
            dataSetReady = true
        }
    }

}