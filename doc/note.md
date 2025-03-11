### 1、异步任务和协程
+ 异步任务

  后台任务 `doInBackground()` 和  任务完成后的回调 `onPostExecute()`

  ```kotlin
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
  ```

  + 协程实现

    协程调度器

  ```kotlin
  val submitBtn = findViewById<Button>(R.id.demo01_submitBtn).also {
      it.setOnClickListener {
          GlobalScope.launch(Dispatchers.Main) {
              val user = withContext(Dispatchers.IO) {
                  userServiceApi.getUser()
              }
              nameTv.text = "id: ${user?.get(1)?.id} => City: ${user?.get(1)?.name}"
          }
      }
  }
  ```

  + 协程+挂起函数
    1. 使用 `suspend`关键字修饰的函数叫作挂起函数；
    2. 挂起函数只能在协程体内或者其他挂起函数内调用；

  ```kotlin
  submitBtn.setOnLongClickListener {
      GlobalScope.launch(Dispatchers.Main) {
          getCities()
      }
      true
  }
  
  private suspend fun getCities() {
      val cities = get()
      show(cities)
  }
  
  private suspend fun get() = withContext(Dispatchers.IO) {
      userServiceApi.getCities()
  }
  
  private fun show(cities: List<City>) {
      nameTv.text = "id: ${cities?.get(2)?.id} => City: ${cities?.get(2)?.name}"
  }
  ```

  