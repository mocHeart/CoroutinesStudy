package com.hg.crs.demo03

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hg.crs.R
import com.hg.crs.demo02.City
import com.hg.crs.demo02.userServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("MissingInflatedId", "StaticFieldLeak", "SetTextI18n")
class Demo03Activity : AppCompatActivity() {

    private val mainScope = MainScope()
    private lateinit var nameTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo01)

        nameTv = findViewById<TextView>(R.id.demo01_nameTv)
        nameTv.text = "Guangzhou"

        val submitBtn = findViewById<Button>(R.id.demo01_submitBtn).also {
            it.setOnClickListener {
                mainScope.launch() {
                    // 在协程作用域里的挂机函数会自动协程支持
                    val cities = userServiceApi.getCities()
                    nameTv.text = "id: ${cities?.get(3)?.id} => City: ${cities?.get(3)?.name}"
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
        mainScope.cancel()
    }
}