package com.hg.crs.test01

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutineTest01 {

    /**
     * runBlocking 将运行的主线程包装成主协程(添加协程支持)
     *             会等待所有子协程执行完毕才结束主线程
     *
     *  Kotlin支持函数名称存在空格，但需要用单反号引起来
     */
    @Test
    fun `test coroutine builder`() = runBlocking {
        val job1 = launch {
            delay(200)
            println("job1 finished.")
        }
        val job2 = async {
            delay(200)
            println("job2 finished.")
            "job2 result"
        }
        println(job2.await())
    }

    /**
     * launch中使用 join()函数等待协程执行完毕再执行下一个
     */
    @Test
    fun `test coroutine join`() = runBlocking {
        val job1 = launch {
            delay(2000)
            println("Job One.")
        }
        // 等待job1执行完毕
        job1.join()
        val job2 = launch {
            delay(200)
            println("Job Two.")
        }
        val job3 = launch {
            delay(200)
            println("Job Three.")
        }
    }

    /**
     * async中使用 await()函数等待协程执行完毕再执行下一个
     */
    @Test
    fun `test coroutine await`() = runBlocking {
        val job1 = async {
            delay(2000)
            println("Job One.")
        }
        // 等待job1执行完毕
        job1.await()
        val job2 = async {
            delay(200)
            println("Job Two.")
        }
        val job3 = async {
            delay(200)
            println("Job Three.")
        }
    }


    /**
     * runBlocking中的挂起函数默认是依次顺序执行的
     * 执行结果：
     *   The result: 49
     *   Completed in 2030 ms
     */
    @Test
    fun `test sync`() = runBlocking {
        val time = measureTimeMillis {
            val one = doOne()
            val two = doTwo()
            println("The result: ${one + two}")
        }
        println("Completed in $time ms")
    }
    private suspend fun doOne(): Int {
        delay(1000)
        return 14
    }
    private suspend fun doTwo(): Int {
        delay(1000)
        return 35
    }

    /**
     * async的结构化并发
     * 执行结果：
     *   The result: 49
     *   Completed in 1027 ms
     */
    @Test
    fun `test combine async`() = runBlocking {
        val time = measureTimeMillis {
            val one = async { doOne() }
            val two = async { doTwo() }
            println("The result: ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    /**
     * CoroutineStart.DEFAULT:
     *   在调度前如果协程被取消，其将直接进入取消响应的状态
     * CoroutineStart.ATOMIC:
     *   协程创建后，立即开始调度，协程执行到第一个挂起点之前不响应取消
     * CoroutineStart.LAZY:
     *   只有协程被需要时，包括主动调用协程的`start`、`join`或者`await`等函数时
     *   才会开始调度，如果调度前就被取消，那么该协程将直接进入异常结束状态
     * CoroutineStart.UNDISPATCHED:
     *   协程创建后立即在当前函数调用栈中执行，直到遇到第一个真正挂起的点
     */
    @Test
    fun `Test start mode`() = runBlocking {
        val job = launch(start = CoroutineStart.DEFAULT) {
            delay(10_000)
            println("Job finished.")
        }
        delay(1000)
        job.cancel()
    }
    @Test
    fun `Test start mode2`() = runBlocking {
        val job = launch(context = Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
            println("Thread: " + Thread.currentThread().name)
        }
    }

    /**
     * 协程的作用域构建器
     *   coroutineScope 将多个协程置于一个协程作用域，
     *   作用域会等待所有子协程执行完毕，
     *   且只要由一个协程异常退出，所有协程都会退出
     */
    @Test
    fun `Test coroutine scope builder`() = runBlocking {
        coroutineScope {
        //supervisorScope {
            val job1 = launch {
                delay(2000)
                println("Job1 finished.")
            }

            val job2 = async {
                delay(200)
                println("Job2 finished.")
                "job2 result"
                throw IllegalArgumentException()
            }
        }
    }

}