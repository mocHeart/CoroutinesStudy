package com.hg.crs.test03

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CoroutineTest03 {

    /**
     * +操作符来实现组合协程上下文中的元素
     */
    @Test
    fun `test CoroutineContext`() = runBlocking<Unit> {
        launch(Dispatchers.Default + CoroutineName("C3Name")) {
            // I'm working in thread DefaultDispatcher-worker-1 @C3Name#2
            println("I'm working in thread ${Thread.currentThread().name}")
        }
    }

    /**
     * 对于新创建的协程，它的 CoroutineContext 会包含一个全新的Job实例，
     * 它会帮助我们控制协程的生命周期。而剩下的元素会从CoroutineContext的父类继承，
     * 该父类可能是另外一个协程或者创建该协程的CoroutineScope。
     *
     * "C3Name#2":StandaloneCoroutine{Active}@5ca17e97~DefaultDispatcher-worker-1 @C3Name#2
     * "C3Name#3":DeferredCoroutine{Active}@21cc92ef~DefaultDispatcher-worker-3 @C3Name#3
     */
    @Test
    fun `test CoroutineContext extend`() = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.IO + CoroutineName("C3Name"))
        val job = scope.launch {
            println("${coroutineContext[Job]}~${Thread.currentThread().name}")
            val result = async {
                println("${coroutineContext[Job]}~${Thread.currentThread().name}")
                "OK"
            }.await()
        }
        job.join()
    }

}