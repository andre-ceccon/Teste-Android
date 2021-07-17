package br.com.ceccon.andre.data.model

import android.os.Parcelable
import androidx.room.Embedded
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    var uId: Int = 0,
    val url: String?,
    val title: String?,
    @Embedded
    val source: Source,
    val author: String?,
    val content: String?,
    val urlToImage: String?,
    val description: String?,
    val publishedAt: String?
): Parcelable