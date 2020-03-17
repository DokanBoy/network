package one.cyanpowered.network.processor

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext

interface PacketProcessor {
    /**
     * [ByteBuf.release] should NOT be called; it's done externally
     *
     * @param ctx the channel handler context
     * @param input the buffer containing the input data
     * @param buffer the buffer to add the data to; will be dynamically-sized
     * @return the processed outbound buffer
     */
    fun processOutbound(ctx: ChannelHandlerContext, input: ByteBuf, buffer: ByteBuf): ByteBuf

    /**
     * [ByteBuf.release] should NOT be called; it's done externally
     *
     * @param ctx the channel handler context
     * @param input the buffer containing the input data
     * @param buffer the buffer to add the data to; will be dynamically-sized
     * @return the processed inbound buffer
     */
    fun processInbound(ctx: ChannelHandlerContext, input: ByteBuf, buffer: ByteBuf): ByteBuf

    companion object
}