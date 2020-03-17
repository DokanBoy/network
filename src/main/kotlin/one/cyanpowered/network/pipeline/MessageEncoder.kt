package one.cyanpowered.network.pipeline

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import one.cyanpowered.network.Message

class MessageEncoder(private val messageHandler: ChannelMessageHandler) : MessageToMessageEncoder<Message>() {
    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, message: Message, out: MutableList<Any>) {
        val protocol = messageHandler.session!!.protocol
        val reg = protocol.getCodecRegistration(message.javaClass)
        val messageBuf = ctx.alloc().buffer().let {
            reg.getCodec<Message>().encode(it, message)
        }
        val headerBuf = ctx.alloc().buffer().also {
            protocol.writeHeader(it, reg, messageBuf)
        }

        out.add(Unpooled.wrappedBuffer(headerBuf, messageBuf))
    }
}