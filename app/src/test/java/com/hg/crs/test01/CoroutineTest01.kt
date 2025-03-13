package com.hg.crs.test01

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

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

}