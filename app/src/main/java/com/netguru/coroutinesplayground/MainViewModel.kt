package com.netguru.coroutinesplayground

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainViewModel : ViewModel() {

    private val service: GitHubService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GitHubService::class.java)
    }
    private val handler = CoroutineExceptionHandler { _, exception ->
        if (exception is HttpException) {
            when(exception.code()){
                404 -> {
                    mutableMessage.value = "User not found"
                }
            }
        } else {
            mutableMessage.value = "Unknown error"
        }
        mutableProgressBarVisibility.value = false
    }
    private val mutableData = MutableLiveData<Data>()
    val data: LiveData<Data> = mutableData
    private val mutableMessage = LiveEvent<String>()
    val message: LiveData<String> = mutableMessage
    private val mutableProgressBarVisibility = MutableLiveData<Boolean>()
    val progressBarVisibility: LiveData<Boolean> = mutableProgressBarVisibility

    fun fetchData(userName: String) {
        viewModelScope.launch(handler) {
            mutableProgressBarVisibility.value = true
            val repos = withContext(Dispatchers.IO) { service.listRepos(userName) }
            val user = withContext(Dispatchers.IO) { service.user(userName) }
            mutableProgressBarVisibility.value = false
            mutableData.value = Data(user, repos)
        }
    }
}