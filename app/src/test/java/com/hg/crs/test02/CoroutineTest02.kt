package com.hg.crs.test02

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import org.junit.Test
import java.io.BufferedReader
import java.io.FileReader

class CoroutineTest02 {

    /**
     * 取消作用域会取消它的子协程
     */
    @Test
    fun `test scope cancel`() = runBlocking<Unit> {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            delay(1000)
            println("Job 1")
        }
        scope.launch {
            delay(1000)
            println("Job 2")
        }
        delay(200)
        scope.cancel()
        delay(2000)
    }


    /**
     * 被取消的子协程并不会影响其余兄弟协程
     */
    @Test
    fun `test brother cancel`() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Default)
        val job1 = scope.launch {
            delay(1000)
            println("Job 1")
        }
        val job2 = scope.launch {
            delay(1000)
            println("Job 2")
        }
        delay(100)
        job1.cancel()
        delay(2000)
    }

    /**
     * 协程通过抛出一个特殊的异常`CancellationException`来处理取消操作
     */
    @Test
    fun `test Ca`() = runBlocking {
        // GlobalScope.launch{} 创建的协程不会继承 runBlocking{} 的协程上下文
        val job1 = GlobalScope.launch {
            try {
                delay(1000)
                println("Job 1")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        delay(100)
        job1.cancel(CancellationException("取消说明"))
        job1.join()
        // = cancel() + join()
        // job1.cancelAndJoin()
    }

    /**
     * CPU密集型任务取消 - 直接取消协程不会生效
     * isActive方式取消
     * `isActive`：是一个可以被使用在CoroutineScope中的扩展属性，
     *            检查Job是否处于活跃状态
     */
    @Test
    fun `test cancel cpu task by isActive`() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5 && isActive) {
                if (System.currentTimeMillis() > nextPrintTime) {
                    println("Job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500
                }
            }
        }
        delay(1300)
        println("Main: I'm tired of waiting")
        job.cancelAndJoin()
        println("Main: Now I can quit.")
    }

    /**
     * CPU密集型任务取消 - 直接取消协程不会生效
     * ensureActive
     * `ensureActive`：如果job处于非活跃状态，这个方法会立即抛出异常
     */
    @Test
    fun `test cancel cpu task by ensureActive`() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) {
                ensureActive()
                if (System.currentTimeMillis() > nextPrintTime) {
                    println("Job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500
                }
            }
        }
        delay(1300)
        println("Main: I'm tired of waiting")
        job.cancelAndJoin()
        println("Main: Now I can quit.")
    }

    /**
     * CPU密集型任务取消 - 直接取消协程不会生效
     * ensureActive
     * `ensureActive`：会检查所在协程的状态，如果已经取消，则抛出`CancellationException`予以响应。
     * 此外，它还会尝试出让线程的执行权，给其他协程提供执行机会
     */
    @Test
    fun `test cancel cpu task by yield`() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) {
                yield()
                if (System.currentTimeMillis() > nextPrintTime) {
                    println("Job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500
                }
            }
        }
        delay(1300)
        println("Main: I'm tired of waiting")
        job.cancelAndJoin()
        println("Main: Now I can quit.")
    }

    /**
     * 取消协程-释放资源-在finally中释放资源
     */
    @Test
    fun `test release resource`() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("Job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                println("Job: I'm running finally")
            }
        }
        delay(1300)
        println("Main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("Main: Now I can quit.")
    }

    /**
     * 取消协程-释放资源-在finally中释放资源
     */
    @Test
    fun `test use function`() = runBlocking {
        // finally中释放资源
        /*
        var br = BufferedReader(FileReader("C:\\Users\\86199\\Desktop\\JY\\2025_Spring\\code\\CoroutinesStudy\\doc\\note.md"))
        with(br) {
            var line: String?
            try {
                while (true) {
                    line = readLine() ?: break
                    println(line)
                }
            } finally {
                close()
            }
        }
        */
        BufferedReader(FileReader("C:\\Users\\86199\\Desktop\\JY\\2025_Spring\\code\\CoroutinesStudy\\doc\\note.md"))
            .use {
                var line: String?
                while (true) {
                    line = it.readLine() ?: break
                    println(line)
                }
            }
    }


    /**
     * 取消作用域会取消它的子协程
     */
    @Test
    fun `test cancel with NonCancellable`() = runBlocking<Unit> {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("Job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                /*
                println("Job: I'm running finally")
                // 处于取消中状态的协程不能够挂起，下面打印不会执行
                delay(1000L)
                println("Job: And I've just delayed for 1 sec because I'm non-cancellable")
                */

                // 当协程被取消后需要调用挂起函数，我们需要将
                // 清理任务的代码放置于NonCancellable CoroutineContext中
                withContext(NonCancellable) {
                    println("Job: I'm running finally")
                    delay(1000L)
                    println("Job: And I've just delayed for 1 sec because I'm non-cancellable")
                }
            }
        }
        delay(1300)
        println("Main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("Main: Now I can quit.")
    }

    /**
     * 很多情况下取消一个协程的理由是它有可能超时
     * `withTimeout` 超时后抛出异常
     */
    @Test
    fun `test deal with timeout`() = runBlocking {
        withTimeout(1300) {
            repeat(1000) { i ->
                println("Job: I'm sleeping $i ...")
                delay(500L)
            }
        }
    }

    /**
     * 很多情况下取消一个协程的理由是它有可能超时
     * `withTimeoutOrNull` 超时后返回Null
     */
    @Test
    fun `test deal with timeout return null`() = runBlocking {
        val result = withTimeoutOrNull(1300) {
            repeat(1000) { i ->
                println("Job: I'm sleeping $i ...")
                delay(500L)
            }
            "Done"
        } ?: "UnDone"
        println("Result is $result ")
    }
}