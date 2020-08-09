package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


enum class MarsApiStatus { LOADING, ERROR, DONE }

class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _status = MutableLiveData<MarsApiStatus>()

    // The external immutable LiveData for the response String
    val status: LiveData<MarsApiStatus>
        get() = _status

    // The internal MutableLiveData for Mars Property
    private val _properties = MutableLiveData<List<MarsProperty>>()

    // The external immutable LiveData for Mars Property
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    //for navigation when user taps on any image
    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main
    )

    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }


    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        /*MarsApi.retrofitService.getProperties().enqueue(
                object: Callback<List<MarsProperty>> {
                    override fun onFailure(call: Call<List<MarsProperty>>, t: Throwable) {
                        _response.value = "Failure: " + t.message
                    }

                    override fun onResponse(call: Call<List<MarsProperty>>, response: Response<List<MarsProperty>>) {
                        _response.value = "Success: ${response.body()?.size} Mars properties retrieved"
                    }
                })*/
        coroutineScope.launch {
            var getPropertiesDeferred = MarsApi.retrofitService.getPropertiesAsync(filter.value)
            try {
                _status.value = MarsApiStatus.LOADING
                var listResult = getPropertiesDeferred.await()
                _status.value = MarsApiStatus.DONE
                _properties.value = listResult
            } catch (e: Exception) {
                _status.value = MarsApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun updateFilter(filter: MarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }

    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToSelectedProperty.value = marsProperty
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
}
