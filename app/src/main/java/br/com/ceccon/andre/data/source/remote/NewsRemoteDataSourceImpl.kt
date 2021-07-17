package br.com.ceccon.andre.data.source.remote

import br.com.ceccon.andre.data.source.remote.model.NetworkNews
import br.com.ceccon.andre.data.source.remote.retrofit.NewsApiService
import br.com.ceccon.andre.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

class NewsRemoteDataSourceImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val apiService: NewsApiService
) : NewsRemoteDataSource {

    override suspend fun getTopHeadLinesNews(): Result<NetworkNews> = withContext(ioDispatcher) {
        return@withContext try {
            val result = apiService.getCategoryTechnology()
            if (result.isSuccessful) {
                val networkWeatherForecast = result.body()
                Result.Success(networkWeatherForecast)
            } else Result.Error(Exception("Error"))
        } catch (exception: Exception) {
            Timber.d("Exception %s", exception.message)
            Result.Error(exception)
        }
    }
}