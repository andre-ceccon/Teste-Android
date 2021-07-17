package br.com.ceccon.andre.data.source.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.ceccon.andre.data.source.local.dao.NewsDao
import br.com.ceccon.andre.data.source.local.entity.DBNews

@Database(entities = [DBNews::class], version = 1, exportSchema = true)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getInstance(
            context: Context
        ): NewsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(
            context: Context
        ) = Room.databaseBuilder(
            context.applicationContext,
            NewsDatabase::class.java,
            "news_db"
        ).fallbackToDestructiveMigration().build()
    }
}