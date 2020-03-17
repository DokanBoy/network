package one.cyanpowered.network.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ReplayingDecoder
import one.cyanpowered.network.Packet
import one.cyanpowered.network.exception.UnknownPacketException

class PacketDecoder(
        private val packetHandler: ChannelPacketHandler
) : ReplayingDecoder<ByteBuf?>() {
    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        val protocol = packetHandler.session!!.protocol
        val codec = try {
            protocol.readHeader(buf)
        } catch (e: UnknownPacketException) {
            val length = e.length
            if (length != -1 && length != 0) {
                buf.readBytes(length)
            }
            throw e
        }
        val decoded: Packet = codec.decode(buf)
        println("Decoded message: $decoded")

        out.add(decoded)
    }
}