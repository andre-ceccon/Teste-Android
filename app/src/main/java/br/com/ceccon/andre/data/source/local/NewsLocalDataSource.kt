package br.com.ceccon.andre.data.source.local

import br.com.ceccon.andre.data.source.local.entity.DBNews

interface NewsLocalDataSource {
    suspend fun deleteAllNews()

    suspend fun getAllNews(): List<DBNews>?

    suspend fun getNews(unique_id: Int): DBNews?

    suspend fun saveNews(dbNews: DBNews): Long
}