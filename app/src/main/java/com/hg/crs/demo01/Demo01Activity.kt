package com.hg.crs.demo01

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hg.crs.R
import com.hg.crs.demo01.api.City
import com.hg.crs.demo01.api.userServiceApi

@SuppressLint("MissingInflatedId", "StaticFieldLeak", "SetTextI18n")
class Demo01Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo01)

        val nameTv = findViewById<TextView>(R.id.demo01_nameTv)
        nameTv.text = "Guangzhou"

        val submitBtn = findViewById<Button>(R.id.demo01_submitBtn).also {
            it.setOnClickListener {
                object : AsyncTask<Void, Void, List<City>>() {
                    // 后台任务
                    override fun doInBackground(vararg params: Void?): List<City>? {
                        return userServiceApi.loadUser().execute().body()
                    }
                    // 回调方法
                    override fun onPostExecute(user: List<City>?) {
                        nameTv.text = "id: ${user?.get(0)?.id} => City: ${user?.get(0)?.name}"
                    }
                }.execute()
            }
        }
    }
}