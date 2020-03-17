package one.cyanpowered.network.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import one.cyanpowered.network.processor.PacketProcessor

class PacketProcessorEncoder(private val packetHandler: ChannelPacketHandler) : MessageToMessageEncoder<ByteBuf>() {
    val processor: PacketProcessor? get() = packetHandler.session?.processor

    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, byteBuf: ByteBuf, output: MutableList<Any>) {
        val processor = processor

        if (processor == null) {
            output.add(byteBuf.readBytes(byteBuf.readableBytes()))
            return
        }

        val toAdd = ctx.alloc().buffer().let {
            processor.processOutbound(ctx, byteBuf, it)
        }

        output.add(toAdd)
    }
}