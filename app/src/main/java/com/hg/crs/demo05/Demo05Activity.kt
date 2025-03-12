package com.hg.crs.demo05

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hg.crs.R
import com.hg.crs.databinding.ActivityDemo05Binding

class Demo05Activity : AppCompatActivity() {

    private val demo05ViewModel: Demo05ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityDemo05Binding>(this, R.layout.activity_demo05)

        binding.viewModel = demo05ViewModel
        binding.lifecycleOwner = this
        binding.demo05SubmitBtn.setOnClickListener {
            demo05ViewModel.getCities()
        }
    }
}