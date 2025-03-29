package com.hg.crs.demo07.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.hg.crs.databinding.FragmentD07DownloadBinding
import com.hg.crs.demo07.download.DownloadManager
import com.hg.crs.demo07.download.DownloadStatus
import com.hjq.toast.Toaster
import java.io.File

class D07DownloadFragment : Fragment() {

    val URL = "http://116.198.231.162:3100/avatars/309b1e19c3824018c46d96da25f1d4652a54b66d0d7aceae1ba20332faed47ce?size=56"

    private val mBinding: FragmentD07DownloadBinding by lazy {
        FragmentD07DownloadBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            context?.apply {
                val file = File(getExternalFilesDir(null)?.path, "pic.jpg")
                DownloadManager.download(URL, file).collect { status ->
                    when (status) {
                        is DownloadStatus.Progress -> {
                            mBinding.apply {
                                progressBar.progress = status.value
                                tvProgress.text = "${status.value}"
                            }
                        }
                        is DownloadStatus.Error -> {
                            Toaster.show("下载错误")
                        }
                        is DownloadStatus.Done -> {
                            mBinding.apply {
                                progressBar.progress = 100
                                tvProgress.text = "100%"
                            }
                        }
                        else -> {
                            Log.d("JY", "下载失败")
                        }
                    }
                }
            }
        }
    }
}