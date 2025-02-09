package com.hg.crs.demo01

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hg.crs.R
import com.hg.crs.demo01.api.User
import com.hg.crs.demo01.api.userServiceApi

@SuppressLint("MissingInflatedId", "StaticFieldLeak")
class Demo01Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo01)

        val nameTv = findViewById<TextView>(R.id.demo01_nameTv)
        nameTv.text = "Jack"

        val submitBtn = findViewById<Button>(R.id.demo01_submitBtn).also {
            it.setOnClickListener {
                object : AsyncTask<Void, Void, User>() {
                    // 后台任务
                    override fun doInBackground(vararg params: Void?): User? {
                        return userServiceApi.loadUser("xiaoming").execute().body()
                    }
                    // 回调方法
                    override fun onPostExecute(user: User?) {
                        nameTv.text = "ABc~123~City: ${user?.city}"
                    }
                }.execute()
            }
        }
    }
}