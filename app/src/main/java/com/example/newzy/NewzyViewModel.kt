package com.example.newzy

import android.util.Log
import androidx.lifecycle.*
import com.example.newzy.database.CountryNewzy
import com.example.newzy.database.FilterData
import com.example.newzy.database.FilterDataDao
import com.example.newzy.network.Articles
import com.example.newzy.network.NewsApi
import kotlinx.coroutines.launch

class NewzyViewModel(private val filterDataDao: FilterDataDao): ViewModel() {

    private val _allArticles = MutableLiveData<List<Articles>>()
    val allArticles : LiveData<List<Articles>> = _allArticles

    val country : LiveData<CountryNewzy> = filterDataDao.getCountry().asLiveData()

    val retrieveFilterData: LiveData<FilterData> = filterDataDao.getLatestFilterData().asLiveData()

    private var _page = MutableLiveData(1)
    val page:LiveData<Int> = _page

    fun updatePage(page: Int) {
        _page.value = page
    }

    fun getSearch(searchTerm: String,pageSize: Int?,page: Int, from: String?, to: String?, sortBy: String?, language: String?) {
        viewModelScope.launch {
            try {
                Log.v("Starting","hello there")
                val obj = NewsApi.retrofitService.getNews(searchTerm,pageSize,page,sortBy,from, to, language)
                _allArticles.value = obj.articles
                Log.v("checking","Done")
            } catch (e: Exception) {
                Log.v("error search","$e")
            }
        }
    }

    fun getTopHeadlines(category: String) {
        viewModelScope.launch {
            try {
                val obj =
                    NewsApi.retrofitService.getTopHeadlines(country.value!!.code,category)
                    _allArticles.value = obj.articles

            } catch (e: Exception) {
                Log.v("error top headlines", "$e")
            }
        }
    }

    fun updateCountry(country: CountryNewzy) {
        viewModelScope.launch {
            filterDataDao.updateCountry(country)
        }
    }

    fun insertCountry(country: CountryNewzy) {
        viewModelScope.launch{
            filterDataDao.insertCountry(country)
        }
    }

    fun updateSlider(value: Float) {
        val oldData = retrieveFilterData
        val valueToInt = value.toInt()
        val newData = FilterData(
            id = oldData.value!!.id,
            sliderCount = valueToInt,
            from = oldData.value?.from,
            to = oldData.value?.to,
            language = oldData.value?.language,
            sortBy = oldData.value?.sortBy
        )
        viewModelScope.launch {
            filterDataDao.updateFilterData(newData)
        }
    }

    fun updateFrom(value: Long?) {
        val oldData = retrieveFilterData
        val newData = FilterData(
            id = oldData.value!!.id,
            sliderCount = oldData.value?.sliderCount,
            from = value,
            to = oldData.value?.to,
            language = oldData.value?.language,
            sortBy = oldData.value?.sortBy
        )
        viewModelScope.launch {
            filterDataDao.updateFilterData(newData)
        }
    }

    fun updateTo(value: Long?) {
        val oldData = retrieveFilterData
        val newData = FilterData(
            id = oldData.value!!.id,
            sliderCount = oldData.value?.sliderCount,
            from = oldData.value?.from,
            to = value,
            language = oldData.value?.language,
            sortBy = oldData.value?.sortBy
        )
        viewModelScope.launch {
            filterDataDao.updateFilterData(newData)
        }
    }

    fun updateSortBy(value: String) {
        val oldData = retrieveFilterData
        val newData = FilterData(
            id = oldData.value!!.id,
            sliderCount = oldData.value?.sliderCount,
            from = oldData.value?.from,
            to = oldData.value?.to,
            language = oldData.value?.language,
            sortBy = value
        )
        viewModelScope.launch {
            filterDataDao.updateFilterData(newData)
        }
    }

    fun updateLanguage(value: String) {
        val oldData = retrieveFilterData
        val newData = FilterData(
            id = oldData.value!!.id,
            sliderCount = oldData.value?.sliderCount,
            from = oldData.value?.from,
            to = oldData.value?.to,
            language = value,
            sortBy = oldData.value?.sortBy
        )
        viewModelScope.launch {
            filterDataDao.updateFilterData(newData)
        }
    }

    fun insertFilterData() {
        val newData = FilterData(
            id = null,
            sliderCount = 20,
            from = null,
            to = null,
            sortBy = null,
            language = null
        )
        viewModelScope.launch {
            filterDataDao.insertFilterData(newData)
        }
    }
}

class NewzyViewModelFactory(private val filterDataDao: FilterDataDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewzyViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return NewzyViewModel(filterDataDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}