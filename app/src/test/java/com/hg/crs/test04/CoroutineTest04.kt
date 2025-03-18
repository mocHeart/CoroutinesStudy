package com.hg.crs.test04

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.yield
import org.junit.Test

class CoroutineTest04 {

    /**
     * launch: 异常-自动传播异常, 在发生出捕获
     * async： 异常-向用户暴露异常，await处捕获异常
     */
    @Test
    fun `test exception propagation`() = runBlocking<Unit> {
        val job = GlobalScope.launch {
            try {
                throw IndexOutOfBoundsException()
            } catch (e: Exception) {
                println("Caught IndexOutOfBoundsException")
            }
        }
        job.join()

        val deferred = GlobalScope.async {
            throw ArithmeticException()
        }
        try {
            deferred.await()
        } catch (e: Exception) {
            println("Caught ArithmeticException")
        }
        delay(1000)
    }


    /**
     * 其他协程所创建的协程中，产生的异常总是会被传播
     * 非根协程异常直接抛出
     */
    @Test
    fun `test exception propagation2`() = runBlocking<Unit> {
        val scope = CoroutineScope(Job())
        val job = scope.launch {
            async {
                throw IllegalArgumentException()
            }
        }
        job.join()
    }

    /**
     * 使用SupervisorJob时，一个子协程的运行失败不会影响到其他子协程。
     * SupervisorJob不会传播异常给它的父级，它会让子协程自己处理异常。
     * 父协程取消，所有子协程也会被取消
     */
    @Test
    fun `test SupervisorJob `() = runBlocking<Unit> {
        //val supervisor = CoroutineScope(Job())
        val supervisor = CoroutineScope(SupervisorJob())
        val job1 = supervisor.launch {
            delay(100)
            println("Child 1")
            throw IllegalArgumentException()
        }
        val job2 = supervisor.launch {
            try {
                delay(Long.MAX_VALUE)
            } finally {
                println("Child 2 finished.")
            }
        }
        delay(2000)
        supervisor.cancel()
        joinAll(job1, job2)
    }

    /**
     * 使用supervisorJob作用域创建的协程和使用SupervisorJob时是一致的
     */
    @Test
    fun `test supervisorJob `() = runBlocking<Unit> {
        supervisorScope {
            launch {
                delay(100)
                println("Child 1")
                throw IllegalArgumentException()
            }
            try {
                delay(Long.MAX_VALUE)
            } finally {
                println("Child 2 finished.")
            }
        }
    }

    /**
     * 使用supervisorJob作用域创建的协程，作用域中抛出的异常会取消所有子协程
     */
    @Test
    fun `test supervisorJob2 `() = runBlocking<Unit> {
        supervisorScope {
            val child1 = launch {
                try {
                    println("The child is sleeping.")
                    delay(Long.MAX_VALUE)
                } finally {
                    println("The child is cancelled")
                }
            }
            yield()
            println("Throwing an exception from the scope.")
            throw AssertionError()
        }
    }
}