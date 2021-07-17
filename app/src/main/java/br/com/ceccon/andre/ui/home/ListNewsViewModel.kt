package br.com.ceccon.andre.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.ceccon.andre.data.model.Article
import br.com.ceccon.andre.data.source.repository.NewsRepository
import br.com.ceccon.andre.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListNewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val news = MutableLiveData<List<Article?>>()
    val dataFetchState = MutableLiveData<Boolean>()

    fun getNews() {
        isLoading.postValue(true)
        viewModelScope.launch {
            when (val result = repository.getAllNews(false)) {
                is Result.Success -> {
                    isLoading.postValue(false)

                    result.data?.let { listArticle ->
                        if (listArticle.isEmpty()) {
                            news.postValue(emptyList())
                            dataFetchState.postValue(false)
                        } else {
                            dataFetchState.postValue(true)
                            news.postValue(listArticle)
                        }
                    }
                }
                is Result.Error -> {
                    isLoading.postValue(false)
                    dataFetchState.postValue(false)
                }

                is Result.Loading -> isLoading.postValue(true)
            }
        }
    }

    fun refreshNews() {
        isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                when (val result = repository.getAllNews(true)) {
                    is Result.Success -> {
                        isLoading.postValue(false)

                        result.data?.let { listArticle ->
                            if (listArticle.isEmpty()) getNews()
                            else {
                                repository.deleteAllArticleData()
                                repository.storeArticleData(listArticle)

                                dataFetchState.postValue(true)
                                news.postValue(listArticle)
                            }
                        } ?: getNews()
                    }
                    is Result.Error -> getNews()
                    is Result.Loading -> isLoading.postValue(true)
                }
            }
        }
    }

    class ViewModelFactory(private val repository: NewsRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ListNewsViewModel::class.java)) {
                return modelClass.getConstructor(NewsRepository::class.java).newInstance(repository)
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}