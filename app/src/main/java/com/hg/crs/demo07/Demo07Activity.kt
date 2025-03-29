package com.hg.crs.demo07

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hg.crs.R
import com.hg.crs.databinding.ActivityDemo07Binding

class Demo07Activity  : AppCompatActivity() {

    private val mBinding: ActivityDemo07Binding by lazy {
        ActivityDemo07Binding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }

}
