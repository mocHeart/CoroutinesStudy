package com.hg.crs.demo07.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hg.crs.databinding.FragmentD07SharedFlowBinding
import com.hg.crs.demo07.viewmodel.SharedFlowViewModel

class D07SharedFlowFragment : Fragment() {

    private val viewModel by viewModels<SharedFlowViewModel>()

    private val mBinding: FragmentD07SharedFlowBinding by lazy {
        FragmentD07SharedFlowBinding.inflate(layoutInflater)
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
            d07BtnStart.setOnClickListener {
                viewModel.startRefresh()
            }
            d07BtnStop.setOnClickListener {
                viewModel.stopRefresh()
            }
        }
    }


}