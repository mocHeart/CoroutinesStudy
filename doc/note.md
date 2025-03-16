### 1. 异步任务和协程
+ **异步任务**

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

  + **协程+挂起函数**
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

### 2. 基础概念

+ **协程是什么？**

  协程基于线程，它是轻量级线程。

  1. 协程让==异步逻辑同步化==，杜绝回调地狱；
  2. 协程最核心的点就是，函数或者一段程序能够被==挂起==，稍后再在挂起的位==恢复==。

+ **协程用来解决什么问题？**

  1. 处理耗时任务，这种任务常常会阻塞主线程；
  2. 保证主线程安全，即确保安全地从主线程调用任何suspend函数。


  + **协程的挂起和恢复**
  
    常规函数基础操作包括：invoke（或cal）和return，协程新增了suspend和resume：
  
    ==suspend==：也称为挂起或暂停，用于暂停执行当前协程，并保存所有局部变量；
    
    ==resume==：用于让已暂停的协程从其暂停处继续执行。
    
    使用suspend关键字修饰的函数叫作挂起函数；
    
    挂起函数只能在协程体内或其他挂起函数内调用。
    
  + **调度器**

    所有协程必须在调度器中运行，即使它们在主线程上运行也是如此。

    <img src=".\img\协程的调度器.png" alt="1" style="zoom:50%;" />

  + **协程的两部分**

    ==基础设施层==： 标准库的协程API，主要对协程提供了概念和语义上最基本的支持；

    ==业务框架层==： 协程的上层框架支持

+ **协程的任务泄漏**

  当某个协程任务丢失，无法追踪，会导致内存、CPU、磁盘等资源浪费，甚至发送一个无用的网络请求，这种情况称为==任务泄漏==。

  为了能够避免协程泄漏，Kotlin引入了==结构化并发机制==。

+ **结构化并发**

  使用结构化并发可以做到：

  1. 取消任务，当某项任务不再需要时取消它；
  2. 追踪任务，当任务正在执行是，追踪它；
  3. 发出错误信号，当协程失败时，发出错误信号表明有错误发生。

+ **CoroutineScope**

  定义协程必须指定其CoroutineScope，它会跟踪所有协程，同样它还可以取消由它所启动的所有协程。

  常用的相关API有：

  1. ==GlobalScope==：生命周期是process级别的，即使Activity或Fragment已经被销毁，协程仍然在执行；
  2. ==MainScope==：在Activity中使用，可以在onDestroy0中取消协程；
  3. ==viewModelScope==：只能在ViewModel中使用，绑定ViewModel的生命周期；
  4. ==lifecycleScope==：只能在Activity、Fragment中使用，会绑定Activity和Fragment的生命周期。

### 3. 协程的启动与取消

#### 3.1 **协程构建器**

+ **launch与async构建器都用来启动新协程**

  ==launch==：返回一个Job并且不附带任何结果值；

  ==async==：返回一个Deferred，Deferred也是一个Job，可以使用.await()在一个延期的值上得到它的最终结果。

+ **等待一个作业**

  join与await
  组合并发

#### 3.2 协程的启动模式

==DEFAUlT==：协程创建后，立即开始调度，在调度前如果协程被取消，其将直接进入取消响应的状态；

==ATOMIC==：协程创建后，立即开始调度，协程执行到第一个挂起点之前不响应取消；

==LAZY==：只有协程被需要时，包括主动调用协程的`start`、`join`或者`await`等函数时才会开始调度，如果调度前就被取消，那么该协程将直接进入异常结束状态；

==UNDISPATCHED==：协程创建后立即在当前函数调用栈中执行，直到遇到第一个真正挂起的点。

#### 3.3 协程的作用域构建器

+ **coroutineScope与runBlocking**

  `runBlocking`是常规函数，而``coroutineScope`是挂起函数；

  它们==都会等待其协程体以及所有子协程结束==，主要区别在于`runBlocking`方法会阻塞当前线程来等待，而`coroutineScope`只是挂起，会释放底层线程用于其他用途。

+ **coroutineScope与supervisorScope**

  coroutineScope：一个协程失败了，所有其他兄弟协程也会被取消；

  supervisorScope：一个协程失败了，不会影响其他兄弟协程。

#### 3.4 Job对象

对于每一个创建的协程（通过launch或者async），会返回一个Job实例，该实例是协程的唯一标示，并且负责管理协程的生命周期。

一个任务可以包含一系列状态：新创建（==New==）、活跃（==Active==）、完成中（==Completing==）、已完成（==Completed==）、取消中（==Cancelling==）和已取消（==Cancelled==）。虽然我们无法直接访问这些状态，但是我们可以访问`job`的属性：`isActive`、`isCancelled`和`isCompleted`。

+ **Job的生命周期**

  如果协程处于活跃状态，协程运行出错或者调用`job.cancel()`都会将当前任务置为取消中（Cancelling）状态（`isActive=false,isCancelled=true`）。当所有的子协程都完成后，协程会进入已取消（Cancelled）状态，此时`isCompleted=true`。

  <img src="img/Job的生命周期.png" alt="4" style="zoom:55%;" />

+ **coroutineScope与CoroutineScope**

  `coroutineScope(){...}`协程的作用域构建器，会继承父协程的作用域，会等待其协程体以及所有子协程结束。

  `val scope=CoroutineScope(Dispatchers.Default)`自定义协程作用域，不会继承父协程的作用域，能通过scope取消协程。

#### 3.5 协程的取消

1. 取消作用域会取消它的子协程；
2. 被取消的子协程并不会影响其余兄弟协程；
3. 协程通过抛出一个特殊的异常`CancellationException`来处理取消操作；
4. 所有`kotlinx.coroutines`中的挂起函数（`withContext`、`delay`等）都是可取消的。	

+ **CPU密集型任务取消**
  1.  `isActive`：是一个可以被使用在CoroutineScope中的扩展属性，检查Job是否处于活跃状态；
  1.  `ensureActive()` 如果job处于非活跃状态，这个方法会立即抛出异常，异常会静默掉，需要try-catch捕获；
  1.  `yield`函数会检查所在协程的状态，如果已经取消，则抛出`CancellationException`予以响应。此外，它还会尝试出让线程的执行权，给其他协程提供执行机会。
+ **协程取消的副作用**
  1. 在finally中释放资源；
  2. ==use()==函数：该函数只能被实现了`Closeable`的对象使用，程序结束的时候会自动调用`close`方法，适合文件对象。
+ **不能取消的任务**
  1. 处于取消中状态的协程不能够挂起（运行不能取消的代码），当协程被取消后需要调用挂起函数，我们需要将清理任务的代码放置于NonCancellable CoroutineContext中。
  2. 这样会挂起运行中的代码，并保持协程的取消中状态直到任务处理完成。
+ **超时任务**
  1. 很多情况下取消一个协程的理由是它有可能超时；
  2. `withTimeoutOrNull`通过返回`null`来进行超时操作，从而替代抛出一个异常。

