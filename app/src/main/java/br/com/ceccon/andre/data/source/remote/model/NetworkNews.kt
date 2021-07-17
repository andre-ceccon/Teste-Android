package br.com.ceccon.andre.data.source.remote.model

import br.com.ceccon.andre.data.model.Article

data class NetworkNews(
    val articles: List<Article> = emptyList()
)
