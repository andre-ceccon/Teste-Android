package br.com.ceccon.andre.mapper

import br.com.ceccon.andre.data.model.Article
import br.com.ceccon.andre.data.source.remote.model.NetworkNews

class NewsMapperRemote : BaseMapper<NetworkNews, List<Article>> {

    override fun mapToEntity(domainModel: List<Article>): NetworkNews {
        return NetworkNews(articles = domainModel)
    }

    override fun mapFromEntity(entity: NetworkNews): List<Article> {
        return entity.articles
    }
}