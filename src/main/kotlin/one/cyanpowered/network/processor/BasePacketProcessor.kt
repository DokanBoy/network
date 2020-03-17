package one.cyanpowered.network.processor

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import kotlin.math.min

abstract class BasePacketProcessor(
        val capacity: Int
) : PacketProcessor {
    private val decodingByteBuffer: ByteArray = ByteArray(capacity)
    private val encodingByteBuffer: ByteArray = ByteArray(capacity)

    @Synchronized
    override fun processOutbound(ctx: ChannelHandlerContext, input: ByteBuf, buffer: ByteBuf): ByteBuf {
        var remaining: Int
        while (input.readableBytes().also { remaining = it } > 0) {
            val clamped = min(remaining, capacity)

            input.readBytes(encodingByteBuffer, 0, clamped)
            writeEncode(encodingByteBuffer, clamped)

            var read: Int
            while (readEncode(encodingByteBuffer).also { read = it } > 0) {
                buffer.writeBytes(encodingByteBuffer, 0, read)
            }
        }
        return buffer
    }

    protected abstract fun writeEncode(buf: ByteArray?, length: Int)
    protected abstract fun readEncode(buf: ByteArray?): Int

    @Synchronized
    override fun processInbound(ctx: ChannelHandlerContext, input: ByteBuf, buffer: ByteBuf): ByteBuf {
        var remaining: Int
        while (input.readableBytes().also { remaining = it } > 0) {
            val clamped = min(remaining, capacity)

            input.readBytes(decodingByteBuffer, 0, clamped)
            writeDecode(decodingByteBuffer, clamped)

            var read: Int
            while (readDecode(decodingByteBuffer).also { read = it } > 0) {
                buffer.writeBytes(decodingByteBuffer, 0, read)
            }
        }
        return buffer
    }

    protected abstract fun writeDecode(buf: ByteArray?, length: Int)
    protected abstract fun readDecode(buf: ByteArray?): Int

}