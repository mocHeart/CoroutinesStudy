package com.hg.crs.demo07.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.hg.crs.databinding.FragmentD07SharedFlowTestBinding
import com.hg.crs.demo07.common.LocalEventBus


class D07SharedFlowTestFragment : Fragment() {



    private val mBinding: FragmentD07SharedFlowTestBinding by lazy {
        FragmentD07SharedFlowTestBinding.inflate(layoutInflater)
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
            LocalEventBus.events.collect {
                mBinding.d07TvTime.text = it.timestamp.toString()
            }
        }
    }
}