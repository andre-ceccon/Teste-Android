package br.com.ceccon.andre.data.source.remote

import br.com.ceccon.andre.data.source.remote.model.NetworkNews
import br.com.ceccon.andre.utils.Result

interface NewsRemoteDataSource {

    suspend fun getTopHeadLinesNews(): Result<NetworkNews>
}
