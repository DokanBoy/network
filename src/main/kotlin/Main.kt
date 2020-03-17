
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import one.cyanpowered.network.*
import one.cyanpowered.network.processor.PacketProcessor
import one.cyanpowered.network.protocol.AbstractProtocol
import one.cyanpowered.network.protocol.KeyedProtocol
import one.cyanpowered.network.session.BaseSession
import one.cyanpowered.network.session.Session
import one.cyanpowered.network.util.readUTF8
import one.cyanpowered.network.util.readVarInt
import one.cyanpowered.network.util.writeUTF8
import one.cyanpowered.network.util.writeVarInt
import java.net.InetSocketAddress
import java.net.SocketAddress

fun main() {
    McServer.bind(InetSocketAddress("0.0.0.0", 25565))
}

object McServer : NetworkServer() {
    override fun sessionInactivated(session: Session) {
        println("disconnected = $session")
    }

    override fun onBindSuccess(address: SocketAddress) {
        println("Started server on $address")
    }

    override fun newSession(channel: Channel): Session {
        return McSession(channel, McHandshake).also {
            println("Connected = $it")
        }
    }
}

data class HandshakePacket(
        val protocolVersion: Int,
        val serverAddress: String,
        val port: Int,
        val nextState: Int
) : Packet {
    companion object : Codec<HandshakePacket> {
        override fun decode(byteBuf: ByteBuf): HandshakePacket = HandshakePacket(
                byteBuf.readVarInt(),
                byteBuf.readUTF8(),
                byteBuf.readUnsignedShort(),
                byteBuf.readVarInt()
        )

        override fun encode(byteBuf: ByteBuf, message: HandshakePacket): ByteBuf = byteBuf.apply {
            writeVarInt(message.protocolVersion)
            writeUTF8(message.serverAddress)
            writeShort(message.port)
            writeVarInt(message.nextState)
        }
    }
}

class StatusRequest(): Packet {
    override fun toString(): String = "Request()"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
    companion object : Codec<StatusRequest> {
        override fun decode(byteBuf: ByteBuf): StatusRequest = StatusRequest()

        override fun encode(byteBuf: ByteBuf, message: StatusRequest): ByteBuf = byteBuf
    }
}

data class StatusResponse(val string: String): Packet {
    companion object : Codec<StatusResponse> {
        override fun decode(byteBuf: ByteBuf): StatusResponse = StatusResponse(byteBuf.readUTF8())

        override fun encode(byteBuf: ByteBuf, message: StatusResponse): ByteBuf = byteBuf.apply {
               writeUTF8(message.string)
        }
    }
}

abstract class Mc15protocol: McProtocol("mc_15") {

}

abstract class McProtocol(name: String) : KeyedProtocol(name) {


    override fun <S : Session, M : Packet> getPacketHandler(message: Class<M>): PacketHandler<S, M>? {
        TODO("Not yet implemented")
    }

    override fun <T : Packet> getCodecRegistration(message: Class<T>): CodecRegistration {
        TODO("Not yet implemented")
    }

    override fun readHeader(byteBuf: ByteBuf): Codec<*> {
        val opcode = byteBuf.readVarInt()
        val codec = getHandlerLookupService() codecLookup.find(opcode)
        return codec
    }

    override fun writeHeader(header: ByteBuf, codecRegistration: CodecRegistration, data: ByteBuf): ByteBuf = header.apply {
        header.writeVarInt(codecRegistration.opcode)
    }
}

object McProcessor : PacketProcessor {
    override fun processOutbound(ctx: ChannelHandlerContext, input: ByteBuf, buffer: ByteBuf): ByteBuf = buffer.apply {
        val length = input.readableBytes()
        writeVarInt(length)
        writeBytes(input)
        println("Outbound packet length: $length")
    }

    override fun processInbound(ctx: ChannelHandlerContext, input: ByteBuf, buffer: ByteBuf): ByteBuf = buffer.apply {
        val length = input.readVarInt()
        writeBytes(input, length)
        println("Inbound packet length: $length")
    }
}

class McSession(channel: Channel, protocol: AbstractProtocol) : BaseSession(channel, protocol) {
    override var processor: PacketProcessor = McProcessor
}