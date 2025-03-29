package com.hg.crs.demo07.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hg.crs.R
import com.hg.crs.databinding.FragmentD7HomeBinding


class D07HomeFragment : Fragment() {

    private val mBinding: FragmentD7HomeBinding by lazy {
        FragmentD7HomeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.apply {
            btnFlowAndDownload.setOnClickListener {
                findNavController().navigate(R.id.action_d7HomeFragment_to_d07DownloadFragment)
            }

            btnFlowAndRoom.setOnClickListener {
                findNavController().navigate(R.id.action_d7HomeFragment_to_d07UserFragment)
            }
        }
    }
}