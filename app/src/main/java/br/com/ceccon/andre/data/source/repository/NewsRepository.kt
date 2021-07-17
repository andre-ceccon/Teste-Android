package br.com.ceccon.andre.data.source.repository

import br.com.ceccon.andre.data.model.Article
import br.com.ceccon.andre.utils.Result

interface NewsRepository {

    suspend fun deleteAllArticleData()

    suspend fun storeArticleData(articles: List<Article>)

    suspend fun getAllNews(refresh: Boolean): Result<List<Article>>
}