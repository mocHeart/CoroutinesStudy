package com.hg.crs.demo05

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hg.crs.demo02.City
import kotlinx.coroutines.launch

class Demo05ViewModel() : ViewModel() {

    val cityLiveData = MutableLiveData<List<City>>()

    private val cityRepository = CityRepository()

    fun getCities() {
        viewModelScope.launch {
            cityLiveData.value = cityRepository.getCities()
        }
    }

}