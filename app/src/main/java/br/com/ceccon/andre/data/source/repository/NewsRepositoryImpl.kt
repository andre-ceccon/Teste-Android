package br.com.ceccon.andre.data.source.repository

import br.com.ceccon.andre.data.model.Article
import br.com.ceccon.andre.data.source.local.NewsLocalDataSource
import br.com.ceccon.andre.data.source.remote.NewsRemoteDataSource
import br.com.ceccon.andre.mapper.NewsMapperLocal
import br.com.ceccon.andre.mapper.NewsMapperRemote
import br.com.ceccon.andre.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

class NewsRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val localDataSource: NewsLocalDataSource,
    private val remoteDataSource: NewsRemoteDataSource
) : NewsRepository {

    override suspend fun deleteAllArticleData() = withContext(ioDispatcher) {
        localDataSource.deleteAllNews()
    }

    override suspend fun storeArticleData(articles: List<Article>) =
        withContext(ioDispatcher) {
            val mapper = NewsMapperLocal()
            mapper.mapToEntity(articles).let { listOfDbForecast ->
                listOfDbForecast.forEach { localDataSource.saveNews(it) }
            }
        }

    override suspend fun getAllNews(refresh: Boolean): Result<List<Article>> =
        withContext(ioDispatcher) {
            if (refresh) {
                val mapper = NewsMapperRemote()

                val response = remoteDataSource.getTopHeadLinesNews()

                Timber.d("List %s", response)

                when (response) {
                    is Result.Success -> {
                        if (response.data != null) {
                            Result.Success(mapper.mapFromEntity(response.data))
                        } else {
                            Result.Success(null)
                        }
                    }

                    is Result.Error -> {
                        Result.Error(response.exception)
                    }

                    else -> Result.Loading
                }
            } else {
                val mapper = NewsMapperLocal()
                val forecast = localDataSource.getAllNews()
                if (forecast != null) {
                    Result.Success(mapper.mapFromEntity(forecast))
                } else {
                    Result.Success(null)
                }
            }
        }
}