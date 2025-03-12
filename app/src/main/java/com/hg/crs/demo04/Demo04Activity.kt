package com.hg.crs.demo04

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hg.crs.R
import com.hg.crs.demo02.cityServiceApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingInflatedId", "StaticFieldLeak", "SetTextI18n")
class Demo04Activity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var nameTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo01)

        nameTv = findViewById<TextView>(R.id.demo01_nameTv)
        nameTv.text = "Guangzhou"

        val submitBtn = findViewById<Button>(R.id.demo01_submitBtn).also {
            it.setOnClickListener {
                launch() {
                    // 在协程作用域里的挂机函数会自动协程支持
                    val cities = cityServiceApi.getCities()
                    nameTv.text = "id: ${cities?.get(4)?.id} => City: ${cities?.get(4)?.name}"
                    try {
                        delay(10000)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消协程
        cancel()
    }
}