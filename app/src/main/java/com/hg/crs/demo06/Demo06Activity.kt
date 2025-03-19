package com.hg.crs.demo06

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hg.crs.R
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Demo06Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo06)

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.d("CS-JY", "Caught $exception")
        }

        findViewById<Button>(R.id.demo06_btn1).also {
            it.setOnClickListener {
                GlobalScope.launch(handler) {
                    Log.d("CS-JY", "On Click1.")
                    "abc".substring(10)
                }
            }
        }

        findViewById<Button>(R.id.demo06_btn2).also {
            it.setOnClickListener {
                GlobalScope.launch() {
                    Log.d("CS-JY", "On Click2.")
                    "abc".substring(10)
                }
            }
        }
    }
}