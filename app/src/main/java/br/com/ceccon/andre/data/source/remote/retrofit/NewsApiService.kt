package br.com.ceccon.andre.data.source.remote.retrofit

import br.com.ceccon.andre.data.source.remote.model.NetworkNews
import br.com.ceccon.andre.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadLinesNews(
        @Query("country") countryCode: String = "br",
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NetworkNews>

    @GET("v2/top-headlines")
    suspend fun getCategoryTechnology(
        @Query("country") countryCode: String = "br",
        @Query("category") category: String = "technology",
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NetworkNews>
}