package com.hg.crs.test06

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class CoroutineTest06 {

    /**
     * 生产-消费者模式中的channel
     */
    @Test
    fun `test know channel`() = runBlocking {
        val channel = Channel<Int>()
        // 生产者
        val producer = GlobalScope.launch {
            var i = 0
            while (true) {
                delay(1000)
                channel.send(++i)
                println("Send $i")
            }
        }
        // 消费者
        val consumer = GlobalScope.launch {
            while (true) {
                val element = channel.receive()
                println("Receive $element")
            }
        }
        joinAll(producer, consumer)
    }

    /**
     * Channel默认没有缓冲区大小，但没有缓冲区或缓冲区满时
     *   `send`就需要挂起
     */
    @Test
    fun `test know channel2`() = runBlocking {
        val channel = Channel<Int>()
        // 生产者
        val producer = GlobalScope.launch {
            var i = 0
            while (true) {
                delay(1000)
                channel.send(++i)
                println("Send $i")
            }
        }

        // 消费者
        val consumer = GlobalScope.launch {
            while (true) {
                delay(2000)
                val element = channel.receive()
                println("Receive $element")
            }
        }
        joinAll(producer, consumer)
    }


    /**
     * `Channel`本身确实像序列，所以我们在读取的时候
     * 可以直接获取一个`Channel`的迭代器`iterator`.
     */
    @Test
    fun `test iterate channel`() = runBlocking {
        val channel = Channel<Int>(Channel.UNLIMITED)
        // 生产者
        val producer = GlobalScope.launch {
            for (x in 1..5) {
                channel.send(x * x)
                println("Send ${x * x}")
            }
        }

        // 消费者
        val consumer = GlobalScope.launch {
//            val iterator = channel.iterator()
//            while (iterator.hasNext()) {
//                val element = iterator.next()
//                println("Receive $element")
//                delay(2000)
//            }
            // 即可写成for in的形式
            for (element in channel) {
                println("Receive $element")
                delay(2000)
            }
        }
        joinAll(producer, consumer)
    }

    /**
     * 通过`produce`方法启动一个生产者协程，并返回一个`ReceiveChannel`
     */
    @Test
    fun `test fast producer channel`() = runBlocking {
        val receiveChannel: ReceiveChannel<Int> = GlobalScope.produce {
            repeat(100) {
                delay(1000)
                send(it)
            }
        }
        val consumer = GlobalScope.launch {
            for (element in receiveChannel) {
                println("Receive $element")
            }
        }
        consumer.join()
    }

    /**
     * 可以用`actor`启动一个消费者协程
     */
    @Test
    fun `test fast consumer channel`() = runBlocking {
        val sendChannel: SendChannel<Int> = GlobalScope.actor {
            while (true) {
                val element = receive()
                println(element)
            }
        }
        val producer = GlobalScope.launch {
            for (i in 0..3) {
                sendChannel.send(i)
            }
        }
        producer.join()
    }

    /**
     * Channel的关闭
     *   对于一个`Channel`，如果我们调用了它的`close`方法，
     *   它会立即停止接收新元素
     */
    @Test
    fun `test close channel`() = runBlocking {
        val channel = Channel<Int>(3)
        // 生产者
        val producer = GlobalScope.launch {
            List(3) {
                channel.send(it)
                println("Send $it")
            }
            channel.close()
            println("""close channel.
                | - CloseForSend: ${channel.isClosedForSend}
                | - CloseForReceive: ${channel.isClosedForReceive}
            """.trimMargin())
        }

        // 消费者
        val consumer = GlobalScope.launch {
            for (element in channel) {
                println("Receive $element")
                delay(1000)
            }
            println("""After Consuming.
                | - CloseForSend: ${channel.isClosedForSend}
                | - CloseForReceive: ${channel.isClosedForReceive}
            """.trimMargin())
        }
        joinAll(producer, consumer)
    }


    /**
     * BroadcastChannel 广播通道
     *    多个接收端不存在互斥行为
     */
    @Test
    fun `test broadcast`() = runBlocking {
        val broadcastChannel = BroadcastChannel<Int>(Channel.BUFFERED)
        val producer = GlobalScope.launch {
            List(3) {
                delay(100)
                broadcastChannel.send(it)
            }
            broadcastChannel.close()
        }

        List(3) { index ->
            GlobalScope.launch {
                val receiveChannel = broadcastChannel.openSubscription()
                for (i in receiveChannel) {
                    println("[#$index] received: $i")
                }
            }
        }.joinAll()

        // 普通通道可转换为广播通道
        val channel = Channel<Int>()
        val broadcastChannel2 = channel.broadcast(3)
    }




}