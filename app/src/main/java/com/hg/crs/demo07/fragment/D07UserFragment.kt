package com.hg.crs.demo07.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hg.crs.databinding.FragmentD07UserBinding
import com.hg.crs.demo07.adapter.UserAdapter
import com.hg.crs.demo07.viewmodel.UserViewModel

class D07UserFragment: Fragment()  {

    private val viewModel by viewModels<UserViewModel>()

    private val mBinding: FragmentD07UserBinding by lazy {
        FragmentD07UserBinding.inflate(layoutInflater)
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
            dm07BtnAddUser.setOnClickListener {
                viewModel.insert(
                    dm07EtUserId.text.toString(),
                    dm07EtFirstName.text.toString(),
                    dm07EtLastName.text.toString()
                )
            }
        }

        context?.let {
            val adapter = UserAdapter(it)
            mBinding.dm07RecyclerView.adapter = adapter
            lifecycleScope.launchWhenCreated {
                viewModel.getAll().collect { value ->
                    adapter.setData(value)
                }
            }
        }

    }

}