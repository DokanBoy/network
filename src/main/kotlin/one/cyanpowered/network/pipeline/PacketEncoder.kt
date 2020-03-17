package one.cyanpowered.network.pipeline

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import one.cyanpowered.network.Packet

class PacketEncoder(private val packetHandler: ChannelPacketHandler) : MessageToMessageEncoder<Packet>() {
    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, packet: Packet, out: MutableList<Any>) {
        val protocol = packetHandler.session!!.protocol
        val reg = protocol.getCodecRegistration(packet.javaClass)
        val messageBuf = ctx.alloc().buffer().let {
            reg.getCodec<Packet>().encode(it, packet)
        }
        val headerBuf = ctx.alloc().buffer().also {
            protocol.writeHeader(it, reg, messageBuf)
        }

        out.add(Unpooled.wrappedBuffer(headerBuf, messageBuf))
    }
}