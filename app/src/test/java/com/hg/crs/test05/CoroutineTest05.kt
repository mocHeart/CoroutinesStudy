package com.hg.crs.test05

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutineTest05 {

    /************************  认识Flow ************************/

    fun simpleList(): List<Int> = listOf(1, 2, 3)

    suspend fun simpleList2(): List<Int> {
        delay(1000)
        return listOf(1, 2, 3)
    }

    fun simpleSequence(): Sequence<Int> = sequence {
        for (i in 1..3) {
            Thread.sleep(1000)
            // 序列中不被允许使用挂起函数
            //delay(1000)
            yield(i)
        }
    }

    /**
     * 集合：返回了多个值，但不是异步的
     */
    @Test
    fun `test multiple values`() {
        simpleList().forEach { value -> println(value) }
    }

    /**
     * 序列：可以返回多个值，但是会阻塞，不允许使用挂起函数
     */
    @Test
    fun `test multiple values2`() {
        simpleSequence().forEach { value -> println(value) }
    }

    /**
     * 集合：返回了多个值，是异步的，但还是一次性返回多个值
     */
    @Test
    fun `test multiple values3`() = runBlocking {
        simpleList2().forEach { value -> println(value) }
    }


    suspend fun simpleFlow() = flow<Int> {
        for (i in 1..3) {
            delay(1000)
            emit(i)  // 发射，产生一个元素
        }
    }

    /**
     * Flow 能做到返回多个值，而且是异步的
     */
    @Test
    fun `test multiple values4`() = runBlocking {
        launch {
            for (k in 1..3) {
                println("I'm not blocked $k")
                delay(1500)
            }
        }
        simpleFlow().collect { value -> println(value) }
    }

    fun simpleFlow2() = flow<Int> {
        println("Flow started")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }

    /**
     * Flow是一种类似于序列的冷流，
     *   flow构建器中的代码直到流被收集的时候才运行
     */
    @Test
    fun `test flow is cold`() = runBlocking {
        val flow = simpleFlow2()
        println("Calling collect...")
        flow.collect { value -> println(value) }
        println("Calling collect again...")
        flow.collect { value -> println(value) }
    }


    /**
     * Flow: 从上游到下游每个过渡操作符都会处理每个发射出的值，
     * 然后再交给末端操作符
     */
    @Test
    fun `test flow continuation`() = runBlocking {
        (1..5).asFlow().filter {
            it % 2 == 0
        }.map {
            "String $it"
        }.collect {
            println("Collect $it")
        }
    }


    /**
     * `flowOf`构建器定义了一个发射固定值集的流；
     * 使用`.asFlow()`扩展函数，可以将各种集合与序列转换为流
     */
    @Test
    fun `test flow builder`() = runBlocking {
        flowOf("one", "two", "three")
            .onEach { delay(1000) }
            .collect { value -> println(value) }

        (1..3).asFlow().collect { value -> println(value) }
    }


    fun simpleFlow3() = flow<Int> {
        println("Flow started ${Thread.currentThread().name}")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }

    /**
     * 流的收集总是在调用协程的上下文中发生，流的该属性称为==上下文保存==
     */
    @Test
    fun `test flow context`() = runBlocking {
        //simpleFlow4()
        simpleFlow3()
            .collect { value ->
                println("Collected $value ${Thread.currentThread().name}" ) }

    }

    fun simpleFlow4() = flow<Int> {
        // 不允许直接在Flow中通过withContext改变上下文
        withContext(Dispatchers.IO) {
            println("Flow started ${Thread.currentThread().name}")
            for (i in 1..3) {
                delay(1000)
                emit(i)
            }
        }
    }

    fun simpleFlow5() = flow<Int> {
        println("Flow started ${Thread.currentThread().name}")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }.flowOn(Dispatchers.Default)


    /**
     * flowOn操作符，该函数用于更改流发射的上下文
     */
    @Test
    fun `test flow on`() = runBlocking {
        simpleFlow5()
            .collect { value ->
                println("Collected $value ${Thread.currentThread().name}" ) }

    }

    fun events() = (1..3)
        .asFlow()
        .onEach { delay(1000) }
        .flowOn(Dispatchers.Default)

    /**
     * 使用`launchIn`替换`collect`我们可以在单独的协程中启动流的收集
     */
    @Test
    fun `test flow launch`() = runBlocking<Unit>{
        val job = events().onEach { event ->
            println("Event: $event ${Thread.currentThread().name}") }
            //.collect {}
            .launchIn(CoroutineScope(Dispatchers.IO))
            //.join()
            //.launchIn(this)  //当前协程执行

        delay(2000)
        job.cancelAndJoin()
    }

    fun simpleFlow6() = flow<Int> {
        for (i in 1..3) {
            delay(1000)
            emit(i)
            println("Emitting $i")
        }
    }

    /**
     * 流采用与协程同样的协作取消。
     * 流的收集可以是当流在一个可取消的挂起函数中挂起的时候取消。
     */
    @Test
    fun `test cancel flow`() = runBlocking<Unit>{
        withTimeoutOrNull(2500) {
            simpleFlow6().collect { value -> println(value) }
        }
        println("Done")
    }

    fun simpleFlow7() = flow<Int> {
        for (i in 1..5) {
            delay(1000)
            // 发送之前会检测流是否被取消
            emit(i)
            println("Emitting $i")
        }
    }

    /**
     * Flow流能别取消
     * 下面取消时抛出异常：BlockingCoroutine was cancelled
     */
    @Test
    fun `test cancel flow check`() = runBlocking<Unit>{
        try {
            simpleFlow7().collect { value ->
                println(value)
                if (value == 3) cancel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        // 协程处于繁忙循环的情况下, 流不会被取消
        (1..5).asFlow().collect { value ->
            println(value)
            if (value == 3) cancel()
        }

        // 明确指示流可取消，繁忙时也能被取消
        (1..5).asFlow().cancellable().collect { value ->
            println(value)
            if (value == 3) cancel()
        }
    }

    fun simpleFlow8() = flow<Int> {
        for (i in 1..3) {
            delay(100)
            // 发送之前会检测流是否被取消
            emit(i)
            println("Emitting $i ${Thread.currentThread().name}")
        }
    }

    /**
     * 背压：生产大于消费
     *    buffer(), 缓存收集数据，并发运行流中发射元素的代码
     *    flowOn(Dispatchers.Default)  并行消费收集元素
     *    .conflate()  每次收集最新的值，未及时处理的会被覆盖
     *    collectLatest 只收集最后一个值
     */
    @Test
    fun `test flow back pressure`() = runBlocking<Unit>{
        val time = measureTimeMillis {
            simpleFlow8()
                //.buffer(50)
                //.flowOn(Dispatchers.Default)
                //.conflate()
                .collectLatest { value ->
                //.collect { value ->
                delay(300)
                println("Collected $value ${Thread.currentThread().name}" )
            }
        }
        println("Collected in $time ms")

    }



    /************************  Flow操作符 ************************/
    suspend fun performRequest(request: Int): String {
        delay(1000)
        return "response $request"
    }


    /**
     * 操作符：
     *   map{}  转换元素
     *   transform 将一个元素转换为多个
     */
    @Test
    fun `test transform flow operator`() = runBlocking<Unit> {
        (1..3).asFlow()
            .map { request -> performRequest(request) }
            .collect { value -> println(value) }

        (1..3).asFlow()
            .transform { request ->
                emit("Making request $request")
                emit(performRequest(request))
            }
            .collect { value -> println(value) }
    }

    fun numbers() = flow<Int> {
        try {
            emit(1)
            emit(2)
            println("This line will not execute")
            emit(3)
        } finally {
            println("Finally in numbers")
        }
    }

    /**
     * 操作符：
     *   take()  限长操作符，至收集指定数量的元素
     */
    @Test
    fun `test limit length operator`() = runBlocking<Unit> {
        numbers().take(2).collect { value -> println(value) }
    }


    /**
     * 末端操作符：
     *   reduce{}  将流规约到单个值
     */
    @Test
    fun `test terminal operator`() = runBlocking<Unit> {
        val sum = (1..5).asFlow()
            .map { it * it }
            .reduce { a, b -> a + b}
        println(sum)
    }

    /**
     * 组合多个流:
     *   zip{} 用于组合两个流中的相关值
     */
    @Test
    fun `test flow zip`() = runBlocking<Unit> {
        val numbers = (1..3).asFlow()
        val strs = flowOf("one", "two", "three")
        numbers.zip(strs) { a, b -> "$a -> $b" }
            .collect { println(it) }
    }

    /**
     * 组合多个流时，需要等待每个元素产生
     */
    @Test
    fun `test flow zip2`() = runBlocking<Unit> {
        val numbers = (1..3).asFlow().onEach { delay(300) }
        val strs = flowOf("one", "two", "three").onEach { delay(400) }
        val startTime = System.currentTimeMillis()
        numbers.zip(strs) { a, b -> "$a -> $b" }.collect {
            println("$it at ${System.currentTimeMillis() - startTime} ms from start")
        }
    }


    fun requestFlow(i: Int) = flow<String> {
        emit("$i: First")
        delay(500)
        emit("$i: Second")
    }

    /**
     * 展平流：
     *   `flatMapConcat`连接模式
     *    1: First at 1030 ms from start
     *    1: Second at 1549 ms from start
     *    2: First at 2552 ms from start
     *    2: Second at 3055 ms from start
     *    3: First at 4069 ms from start
     *    3: Second at 4585 ms from start
     */
    @Test
    fun `test flatMapConcat`() = runBlocking<Unit> {
        val startTime = System.currentTimeMillis()
        (1..3).asFlow()
            .onEach { delay(1000) }
            .flatMapConcat { requestFlow(it) }
            .collect { println("$it at ${System.currentTimeMillis() - startTime} ms from start")}
    }

    /**
     * 展平流：
     *   `flatMapMerge`合并模式
     *    1: First at 158 ms from start
     *    2: First at 248 ms from start
     *    3: First at 359 ms from start
     *    1: Second at 668 ms from start
     *    2: Second at 761 ms from start
     *    3: Second at 872 ms from start
     */
    @Test
    fun `test flatMapMerge`() = runBlocking<Unit> {
        val startTime = System.currentTimeMillis()
        (1..3).asFlow()
            .onEach { delay(100) }
            .flatMapMerge { requestFlow(it) }
            .collect { println("$it at ${System.currentTimeMillis() - startTime} ms from start")}
    }

    /**
     * 展平流：
     *   `flatMapLatest`最新展平模式
     *    1: First at 159 ms from start
     *    2: First at 297 ms from start
     *    3: First at 411 ms from start
     *    3: Second at 925 ms from start
     */
    @Test
    fun `test flatMapLates`() = runBlocking<Unit> {
        val startTime = System.currentTimeMillis()
        (1..3).asFlow()
            .onEach { delay(100) }
            .flatMapLatest { requestFlow(it) }
            .collect { println("$it at ${System.currentTimeMillis() - startTime} ms from start")}
    }

}