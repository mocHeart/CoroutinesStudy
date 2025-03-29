package com.hg.crs.demo07.download

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import com.hg.crs.demo07.utils.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException

object DownloadManager {
    fun download(url: String, file: File): Flow<DownloadStatus> {
        return flow {
            val request = Request.Builder().url(url).get().build()
            val response = OkHttpClient.Builder().build().newCall(request).execute()
            if (response.isSuccessful) {
                response.body()!!.let { body ->
                    val total = body.contentLength()
                    // 文件读写
                    file.outputStream().use { output ->
                        val input = body.byteStream()
                        var emittedProgress = 0L
                        input.copyTo(output) { bytesCopied ->
                            val progress = bytesCopied * 100 / total
                            if (progress - emittedProgress > 5) {
                                delay(200)
                                emit(DownloadStatus.Progress(progress.toInt()))
                                emittedProgress = progress
                            }
                        }
                    }
                }
                emit(DownloadStatus.Done(file))
            } else {
                throw IOException(response.toString())
            }
        }.catch {
            file.delete()
            emit(DownloadStatus.Error(it))
        }.flowOn(Dispatchers.IO)
    }
}