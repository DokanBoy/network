package one.cyanpowered.network.session

import one.cyanpowered.network.Message
import one.cyanpowered.network.exception.ChannelClosedException
import one.cyanpowered.network.processor.MessageProcessor
import one.cyanpowered.network.protocol.Protocol
import org.slf4j.Logger

interface Session {
    fun <M : Message> messageReceived(message: M)

    val logger: Logger
    val protocol: Protocol
    val processor: MessageProcessor? get() = null

    @Throws(ChannelClosedException::class)
    fun send(message: Message)

    @Throws(ChannelClosedException::class)
    fun sendAll(vararg messages: Message)

    fun disconnect()
    fun onDisconnect()
    fun onReady()

    fun onInboundThrowable(throwable: Throwable)

    companion object
}