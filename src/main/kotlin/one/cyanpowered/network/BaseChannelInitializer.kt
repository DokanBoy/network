package one.cyanpowered.network

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import one.cyanpowered.network.pipeline.*

class BaseChannelInitializer(
        val connectionManager: ConnectionManager
) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        val handler = ChannelPacketHandler(connectionManager)
        val processorDecoder = PacketProcessorDecoder(handler)
        val processorEncoder = PacketProcessorEncoder(handler)
        val decoder = PacketDecoder(handler)
        val encoder = PacketEncoder(handler)

        ch.pipeline()
                .addLast("processorDecoder", processorDecoder)
                .addLast("decoder", decoder)
                .addLast("processorEncoder", processorEncoder)
                .addLast("encoder", encoder)
                .addLast("handler", handler)
    }
}