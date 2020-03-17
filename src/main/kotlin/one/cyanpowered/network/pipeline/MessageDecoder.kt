package one.cyanpowered.network.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder
import one.cyanpowered.network.Message
import one.cyanpowered.network.exception.UnknownMessageException

class MessageDecoder(
        private val messageHandler: ChannelMessageHandler
) : ReplayingDecoder<ByteBuf?>() {
    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        val protocol = messageHandler.session!!.protocol
        val codec = try {
            protocol.readHeader(buf)
        } catch (e: UnknownMessageException) {
            val length = e.length
            if (length != -1 && length != 0) {
                buf.readBytes(length)
            }
            throw e
        }
        val decoded: Message = codec.decode(buf)
        println("Decoded message: $decoded")

        out.add(decoded)
    }
}