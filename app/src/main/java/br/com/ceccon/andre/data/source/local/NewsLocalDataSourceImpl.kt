package br.com.ceccon.andre.data.source.local

import br.com.ceccon.andre.data.source.local.dao.NewsDao
import br.com.ceccon.andre.data.source.local.entity.DBNews
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

class NewsLocalDataSourceImpl(
    private val newsDao: NewsDao,
    private val ioDispatcher: CoroutineDispatcher
) : NewsLocalDataSource {

    override suspend fun deleteAllNews() = withContext(ioDispatcher) {
        Timber.d("deleteAllNews")
        newsDao.deleteAllNews()
    }

    override suspend fun getAllNews(): List<DBNews> = withContext(ioDispatcher) {
        Timber.d("getAllNews")
        return@withContext newsDao.getAllNews()
    }

    override suspend fun saveNews(dbNews: DBNews) = withContext(ioDispatcher) {
        Timber.d("getAllNews")
        newsDao.insertNews(dbNews)
    }

    override suspend fun getNews(unique_id: Int): DBNews = withContext(ioDispatcher) {
        return@withContext newsDao.getNews(unique_id = unique_id)
    }
}