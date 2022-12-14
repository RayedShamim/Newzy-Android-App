package com.example.newzy

import android.util.Log
import androidx.lifecycle.*
import com.example.newzy.database.CountryNewzy
import com.example.newzy.database.FilterData
import com.example.newzy.database.FilterDataDao
import com.example.newzy.network.Articles
import com.example.newzy.network.NewsApi
import kotlinx.coroutines.launch

enum class NewsApiStatus { LOADING, DONE, ERROR }

class NewzyViewModel(private val filterDataDao: FilterDataDao): ViewModel() {

    private val _allArticles = MutableLiveData<List<Articles>>()
    val allArticles : LiveData<List<Articles>> = _allArticles

    val country : LiveData<CountryNewzy> = filterDataDao.getCountry().asLiveData()

    val retrieveFilterData: LiveData<FilterData> = filterDataDao.getLatestFilterData().asLiveData()

    private var _page = MutableLiveData(1)
    val page:LiveData<Int> = _page

    private val _status = MutableLiveData<NewsApiStatus>()
    val status: LiveData<NewsApiStatus> = _status

    private var _searchQuery = MutableLiveData<String?>()
    val searchQuery: LiveData<String?> = _searchQuery

    var cardUpValue: Float = 0f
    var cardheight: Float = 0f
    var cardDownValue: Float = 0f

    init {
        country.value?.let { getTopHeadlines("", it) }
    }

    fun updateSearchQuery(searchTerm: String?) {
        _searchQuery.value = searchTerm
    }

    fun updatePage(page: Int) {
        Log.v("it rannnn","")
        _page.value = page
    }

    fun getSearch(searchTerm: String,pageSize: Int?,page: Int, from: String?, to: String?, sortBy: String?, language: String?) {
        viewModelScope.launch {
            _status.value = NewsApiStatus.LOADING
            try {
                Log.v("Starting","hello there")
                val obj = NewsApi.retrofitService.getNews(searchTerm,pageSize,page,sortBy,from, to, language)
                _allArticles.value = obj.articles
                _status.value = NewsApiStatus.DONE
                Log.v("checking","${status.value}")
            } catch (e: Exception) {
                _status.value = NewsApiStatus.ERROR
                _allArticles.value = listOf()
                Log.v("error search","$e")
            }
        }
    }

    fun getTopHeadlines(category: String,country: CountryNewzy) {
        viewModelScope.launch {
            _status.value = NewsApiStatus.LOADING
            try {
                val obj = NewsApi.retrofitService.getTopHeadlines(country.code,category)
                _allArticles.value = obj.articles
                _status.value = NewsApiStatus.DONE
                Log.v("checking","${status.value}")
            } catch (e: Exception) {
                _status.value = NewsApiStatus.ERROR
                _allArticles.value = listOf()
                Log.v("error top headlines", "$e")
                Log.v("checking","${status.value}")
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
            language = "en"
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