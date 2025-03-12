package com.hg.crs.demo05

import com.hg.crs.demo02.City
import com.hg.crs.demo02.cityServiceApi

class CityRepository {

    suspend fun getCities(): List<City> {
        return cityServiceApi.getCities()
    }

}