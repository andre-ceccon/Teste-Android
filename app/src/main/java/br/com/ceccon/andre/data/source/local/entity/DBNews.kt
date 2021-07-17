package br.com.ceccon.andre.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.ceccon.andre.data.model.Article

@Entity(tableName = "news_table")
data class DBNews(

    @ColumnInfo(name = "unique_id")
    @PrimaryKey(autoGenerate = true)
    var uId: Int = 0,

    @Embedded
    val article: Article
)