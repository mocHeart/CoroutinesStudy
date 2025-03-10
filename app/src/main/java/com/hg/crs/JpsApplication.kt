package com.hg.jps

import android.app.Application
import com.hjq.toast.Toaster

class JpsApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        // 初始化 Toast 框架
        Toaster.init(this);
    }
}