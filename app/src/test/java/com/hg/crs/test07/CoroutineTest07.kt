package com.hg.crs.test07

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.hg.crs.demo02.City
import com.hg.crs.demo02.cityServiceApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import org.junit.Test
import java.io.File


private const val cachePath = "C:\\Users\\86199\\Desktop\\JY\\2025_Spring\\code\\CoroutinesStudy\\doc\\test\\coroutine.json"
private val gson = Gson()

data class Response<T>(val value:T, val isLocal: Boolean)

fun CoroutineScope.getCityFromLocal(name: String) = async(Dispatchers.IO) {
    delay(2000)

    var cityList = mutableListOf<City>()
    File(cachePath).readText().let {
        cityList = Gson().fromJson(it, object : TypeToken<List<City>>() {}.type)
    }
    cityList
}

fun CoroutineScope.getCityFromRemote(name: String) = async(Dispatchers.IO) {
    cityServiceApi.getCities()
}

class CoroutineTest07 {

    /**
     * 复用多个await
     *   分别从本地或远程获取数据，谁先获取到就用谁
     */
    @Test
    fun `test select await`() = runBlocking {
        GlobalScope.launch {
            val localRequest = getCityFromLocal("x")
            val remoteRequest = getCityFromRemote("x")

            val cityResponse = select<Response<List<City>>> {
                localRequest.onAwait { Response(it, true) }
                remoteRequest.onAwait { Response(it, false) }
            }
            cityResponse.value.let { println(it) }
        }.join()
    }


    /**
     * 复用多个Channel
     *   跟`await`类似，会接收到最快的那个`channel`消息
     */
    @Test
    fun `test select channel`() = runBlocking<Unit> {
        val channels = listOf(Channel<Int>(), Channel<Int>())
        GlobalScope.launch {
            delay(100)
            channels[0].send(200)
        }
        GlobalScope.launch {
            delay(50)
            channels[1].send(100)
        }
        val result = select<Int?> {
            channels.forEach { channel ->
                channel.onReceive { it }
            }
        }
        println(result)
    }


    /**
     * SelectClause0：对应事件没有返回值，例如`join`没有返回值，
     *   那么`onJoin`就是`SelectClause N`类型。
     *   使用时，` onJoin`的参数是一个无参函数
     */
    @Test
    fun `test SelectClause0`() = runBlocking<Unit> {
        val job1 = GlobalScope.launch {
            delay(100)
            println("Job 1")
        }
        val job2 = GlobalScope.launch {
            delay(10)
            println("Job 2")
        }
        select<Unit> {
            job1.onJoin { println("Job 1 onJoin") }
            job2.onJoin { println("Job 2 onJoin") }
        }
        delay(1000)
    }


    /**
     * SelectClause2：对应事件有返回值，此外还需要一个额外的参数，
     *    例如`Channel.onSend`有两个参数，第一个是`Channel`数据类型的值，
     *    表示即将发送的值；第二个是发送成功时的回调参数
     */
    @Test
    fun `test SelectClause2`() = runBlocking<Unit> {
        val channels = listOf(Channel<Int>(), Channel<Int>())
        println(channels)

        launch(Dispatchers.IO) {
            select<Unit?> {
                launch {
                    delay(10)
                    channels[1].onSend(200) { sentChannel ->
                        println("Sent on $sentChannel")
                    }
                }
                launch {
                    delay(100)
                    channels[0].onSend(100) { sentChannel ->
                        println("Sent on $sentChannel")
                    }
                }
            }
        }
        GlobalScope.launch {
            println(channels[0].receive())
        }
        GlobalScope.launch {
            println(channels[1].receive())
        }
        delay(1000)
    }


    /**
     * 使用Flow实现多路复用
     */
    @Test
    fun `test select flow`() = runBlocking<Unit> {
        // 函数 -> 协程 -> Flow -> Flow合并
        val name = "guest"
        coroutineScope {
            listOf(::getCityFromLocal, ::getCityFromRemote)
                .map { function ->
                    function.call(name)
                }.map { deferred ->
                    flow { emit(deferred.await()) }
                }.merge().collect{ println(it) }
        }
    }
}