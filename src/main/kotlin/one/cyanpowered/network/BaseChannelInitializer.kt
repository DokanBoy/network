package one.cyanpowered.network

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import one.cyanpowered.network.pipeline.*

class BaseChannelInitializer(
        val connectionManager: ConnectionManager
) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        val handler = ChannelMessageHandler(connectionManager)
        val processorDecoder = MessageProcessorDecoder(handler)
        val processorEncoder = MessageProcessorEncoder(handler)
        val decoder = MessageDecoder(handler)
        val encoder = MessageEncoder(handler)

        ch.pipeline()
                .addLast("processorDecoder", processorDecoder)
                .addLast("decoder", decoder)
                .addLast("processorEncoder", processorEncoder)
                .addLast("encoder", encoder)
                .addLast("handler", handler)
    }
}