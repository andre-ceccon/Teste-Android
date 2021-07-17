package br.com.ceccon.andre.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.ceccon.andre.data.source.local.entity.DBNews

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(dbNews: DBNews): Long

    @Query("SELECT * FROM news_table where unique_id = :unique_id")
    suspend fun getNews(unique_id: Int): DBNews

    @Query("SELECT * FROM news_table ORDER BY title")
    suspend fun getAllNews(): List<DBNews>

    @Query("DELETE FROM news_table")
    suspend fun deleteAllNews()
}