package br.com.ceccon.andre.mapper

import br.com.ceccon.andre.data.model.Article
import br.com.ceccon.andre.data.source.local.entity.DBNews

class NewsMapperLocal : BaseMapper<List<DBNews>, List<Article>> {

    override fun mapToEntity(domainModel: List<Article>): List<DBNews> {
        return domainModel.map { article -> DBNews(article = article) }
    }

    override fun mapFromEntity(entity: List<DBNews>): List<Article> {
        return entity.map { dbNews ->
            with(dbNews.article) {
                Article(
                    url = url,
                    title = title,
                    author = author,
                    source = source,
                    uId = dbNews.uId,
                    content = content,
                    urlToImage = urlToImage,
                    description = description,
                    publishedAt = publishedAt
                )
            }
        }
    }
}