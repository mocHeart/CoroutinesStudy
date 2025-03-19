package com.hg.crs

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hg.crs.demo01.Demo01Activity
import com.hg.crs.demo02.Demo02Activity
import com.hg.crs.demo03.Demo03Activity
import com.hg.crs.demo04.Demo04Activity
import com.hg.crs.demo05.Demo05Activity
import com.hg.crs.demo06.Demo06Activity

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

        findViewById<Button>(R.id.btn_dem04).setOnClickListener{
            intent = Intent(this, Demo04Activity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_dem05).setOnClickListener{
            intent = Intent(this, Demo05Activity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_dem06).setOnClickListener{
            intent = Intent(this, Demo06Activity::class.java)
            startActivity(intent)
        }
    }
}