package com.hg.crs.test08

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class CoroutineTest08 {

    /**
     * 协程操作共享变量-不安全
     */
    @Test
    fun `test not safe concurrent`() = runBlocking {
        var count = 0
        List(1000) {
            GlobalScope.launch { count++ }
        }.joinAll()
        // 997
        println(count)
    }

    /**
     * 协程操作共享变量-原子类
     */
    @Test
    fun `test safe concurrent`() = runBlocking {
        var count = AtomicInteger(0)
        List(1000) {
            GlobalScope.launch { count.incrementAndGet() }
        }.joinAll()
        // 997
        println(count.get())
    }


    /**
     * 协程操作共享变量-轻量级锁，不会阻塞线程
     */
    @Test
    fun `test safe concurrent tools`() = runBlocking {
        var count = 0
        val mutex = Mutex()
        List(1000) {
            GlobalScope.launch {
                mutex.withLock {
                    count++
                }
            }
        }.joinAll()
        // 1000
        println(count)
    }

    /**
     * 协程操作共享变量-轻量级信号量
     */
    @Test
    fun `test safe concurrent tools2`() = runBlocking {
        var count = 0
        val semaphore = Semaphore(1)
        List(1000) {
            GlobalScope.launch {
                semaphore.withPermit {
                    count++
                }
            }
        }.joinAll()
        // 1000
        println(count)
    }

    /**
     * 并发安全-避免访问外部可变状态
     */
    @Test
    fun `test avoid access outer variable`() = runBlocking {
        var count = 0
        val result = count + List(1000) {
            GlobalScope.async { 1 }
        }.map { it.await() }.sum()
        println(result)
    }

}