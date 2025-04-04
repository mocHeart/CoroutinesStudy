package com.hg.crs.demo07.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hg.crs.databinding.FragmentD07NumberBinding
import com.hg.crs.demo07.viewmodel.NumberViewModel

class D07NumberFragment : Fragment() {

    private val viewModel by viewModels<NumberViewModel>()

    private val mBinding: FragmentD07NumberBinding by lazy {
        FragmentD07NumberBinding.inflate(layoutInflater)
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
            d07BtnPlus.setOnClickListener {
                viewModel.increment()
            }
            d07BtnMinus.setOnClickListener {
                viewModel.decrement()
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.number.collect() { value ->
                mBinding.d07TvNumber.text = "$value"
            }
        }
    }

}