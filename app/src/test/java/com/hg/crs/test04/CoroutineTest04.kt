package com.hg.crs.test04

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import okio.IOException
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
    fun `test SupervisorJob`() = runBlocking<Unit> {
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
    fun `test supervisorJob`() = runBlocking<Unit> {
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
    fun `test supervisorJob2`() = runBlocking<Unit> {
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


    /**
     * launch-自动传播的异常能够被handler捕获
     * async-向用户暴露的异常不能够被handler捕获
     */
    @Test
    fun `test CoroutineExceptionHandler`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val job = GlobalScope.launch(handler) {
            throw AssertionError()
        }
        val deferred = GlobalScope.async(handler) {
            throw ArithmeticException()
        }
        job.join()
        deferred.await()
    }

    /**
     * 在`CoroutineScope`的`CoroutineContext`上下文中，
     *  异常处理器Handler在外部协程，异常能被捕获到
     */
    @Test
    fun `test CoroutineExceptionHandler2`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val scope = CoroutineScope(Job())
        // 能被捕获到异常
        val job = scope.launch(handler) {
            launch {
                throw IllegalArgumentException()
            }
        }
        job.join()
    }

    /**
     * 在`CoroutineScope`的`CoroutineContext`上下文中，
     *  异常处理器Handler在内部协程，异常不能被捕获到
     */
    @Test
    fun `test CoroutineExceptionHandler3`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val scope = CoroutineScope(Job())
        // 不能被捕获到异常
        val job = scope.launch {
            launch(handler) {
                throw IllegalArgumentException()
            }
        }
        job.join()
    }

    /**
     * 当子协程被取消时，不会取消它的父协程
     */
    @Test
    fun `test cancel and exception`() = runBlocking<Unit> {
        val job = launch {
            val child = launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    println("Child is cancelled.")
                }
            }
            yield()
            println("Cancelling child.")
            child.cancelAndJoin()
            yield()
            println("Parent is not cancelled.")
        }
        job.join()
    }

    /**
     * 协程遇到了`CancellationException`以外的异常，
     * 该异常将取消父协程。
     * 当父协程的所有子协程都结束后，异常才会被父协程处理
     */
    fun `test cancel and exception2`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val job = GlobalScope.launch(handler) {
            launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    withContext(NonCancellable) {
                        println("Children are cancelled, but exception is not handled until all children terminate")
                        delay(100)
                        println("The first child finished its non cancellable block")
                    }
                }
            }
            launch {
                delay(10)
                println("Second child throws an exception.")
                throw ArithmeticException()
            }
        }
        job.join()
    }

    /**
     * 异常的聚合
     */
    @Test
    fun `test exception aggregation`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception ${exception.suppressed.contentToString()}")
        }
        val job = GlobalScope.launch(handler) {
            launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    throw ArithmeticException()  // 2
                }
            }
            launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    throw IndexOutOfBoundsException()  // 3
                }
            }
            launch {
                delay(100)
                throw IOException()  // 1
            }
        }
        job.join()
    }

}