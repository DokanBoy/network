package one.cyanpowered.network.session

import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import one.cyanpowered.network.Message
import one.cyanpowered.network.MessageHandler
import one.cyanpowered.network.exception.ChannelClosedException
import one.cyanpowered.network.protocol.AbstractProtocol
import org.slf4j.Logger
import java.util.*

open class BaseSession(
        val channel: Channel,
        override val protocol: AbstractProtocol
) : Session {
    val uniqueId = UUID.randomUUID()
    override val logger: Logger
        get() = protocol.logger
    val isActive: Boolean
        get() = channel.isActive

    private fun handleMessage(message: Message) {
        val messageClass = message.javaClass
        val handler = protocol.getMessageHandler<Session,Message>(messageClass)
        if (handler != null) {
            try {
                handler.handle(this, message)
            } catch (t: Throwable) {
                onHandlerThrowable(message, handler, t)
            }
        }
    }

    override fun <M : Message> messageReceived(message: M) {
        handleMessage(message)
    }

    @Throws(ChannelClosedException::class)
    fun sendWithFuture(message: Message): ChannelFuture {
        if (!channel.isActive) {
            throw ChannelClosedException("Trying to send a message when a session is inactive!")
        }
        return channel.writeAndFlush(message).addListener {
            if (it.cause() != null) {
                onOutboundThrowable(it.cause())
            }
        }
    }

    override fun send(message: Message) {
        sendWithFuture(message)
    }

    override fun sendAll(vararg messages: Message) {
        for (message in messages) {
            send(message)
        }
    }

    override fun disconnect() {
        channel.close()
    }

    override fun onDisconnect() {
    }

    override fun onReady() {
    }

    override fun onInboundThrowable(throwable: Throwable) {}

    fun onOutboundThrowable(throwable: Throwable?) {}


    fun onHandlerThrowable(message: Message, handler: MessageHandler<*, *>, throwable: Throwable) {}
}