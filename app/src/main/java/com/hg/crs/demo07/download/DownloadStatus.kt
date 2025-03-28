package com.hg.crs.demo07.download

import java.io.File

sealed class DownloadStatus {
    object None: DownloadStatus()
    data class Progress(val value: Int): DownloadStatus()
    data class Error(val throwable: Throwable): DownloadStatus()
    data class Done(val file: File): DownloadStatus()
}