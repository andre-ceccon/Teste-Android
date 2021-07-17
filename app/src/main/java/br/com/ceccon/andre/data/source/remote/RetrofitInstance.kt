package br.com.ceccon.andre.data.source.remote

import br.com.ceccon.andre.data.source.remote.retrofit.NewsApiService
import br.com.ceccon.andre.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

object RetrofitInstance {

    private fun createRetrofit(): NewsApiService? {
        return try {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsApiService::class.java)
        } catch (e: Exception) {
            Timber.d("Erro na criação do NewsApiService: %s", e.message)
            null
        }
    }

    val api: NewsApiService? by lazy { createRetrofit() }
}