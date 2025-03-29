package com.hg.crs.demo07.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hg.crs.databinding.FragmentD07DownloadBinding

class D07DownloadFragment : Fragment() {

    private val mBinding: FragmentD07DownloadBinding by lazy {
        FragmentD07DownloadBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }
}