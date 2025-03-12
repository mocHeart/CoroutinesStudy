package com.hg.crs

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hg.crs.demo01.Demo01Activity
import com.hg.crs.demo02.Demo02Activity
import com.hg.crs.demo03.Demo03Activity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_dem01).setOnClickListener{
            intent = Intent(this, Demo01Activity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_dem02).setOnClickListener{
            intent = Intent(this, Demo02Activity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_dem03).setOnClickListener{
            intent = Intent(this, Demo03Activity::class.java)
            startActivity(intent)
        }
    }
}