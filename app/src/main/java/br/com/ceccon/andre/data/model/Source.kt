package br.com.ceccon.andre.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Source(
    var id: String? = null, val name: String?
): Parcelable