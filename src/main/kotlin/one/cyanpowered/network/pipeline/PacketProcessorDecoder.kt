package one.cyanpowered.network.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import one.cyanpowered.network.processor.PacketProcessor

class PacketProcessorDecoder(private val packetHandler: ChannelPacketHandler) : ByteToMessageDecoder() {
    val processor: PacketProcessor? get() = packetHandler.session?.processor

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, frames: MutableList<Any>) {
        val processor = processor

        if (processor == null) {
            frames.add(buf.readBytes(buf.readableBytes()))
            return
        }

        // Eventually, we will run out of bytes and a ReplayableError will be called
        val liveBuffer = ctx.alloc().buffer().let {
            processor.processInbound(ctx, buf, it)
        }

        frames.add(liveBuffer)
    }
}