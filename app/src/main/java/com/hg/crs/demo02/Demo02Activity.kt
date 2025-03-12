package com.hg.crs.demo02

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hg.crs.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("MissingInflatedId", "StaticFieldLeak", "SetTextI18n")
class Demo02Activity : AppCompatActivity() {

    private lateinit var nameTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo01)

        nameTv = findViewById<TextView>(R.id.demo01_nameTv)
        nameTv.text = "Guangzhou"

        val submitBtn = findViewById<Button>(R.id.demo01_submitBtn).also {
            it.setOnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    val cities = withContext(Dispatchers.IO) {
                        cityServiceApi.getCities()
                    }
                    nameTv.text = "id: ${cities?.get(1)?.id} => City: ${cities?.get(1)?.name}"
                }
            }
        }

        submitBtn.setOnLongClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                getCities()
            }
            true
        }
    }

    private suspend fun getCities() {
        val cities = get()
        show(cities)
    }

    private suspend fun get() = withContext(Dispatchers.IO) {
        cityServiceApi.getCities()
    }

    private fun show(cities: List<City>) {
        nameTv.text = "id: ${cities?.get(2)?.id} => City: ${cities?.get(2)?.name}"
    }
}