package one.cyanpowered.network.service

import one.cyanpowered.network.Codec
import one.cyanpowered.network.CodecRegistration
import one.cyanpowered.network.Packet
import one.cyanpowered.network.exception.IllegalOpcodeException
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

class CodecLookupService(size: Int) {
    private val packets: ConcurrentMap<Class<out Packet>, CodecRegistration>
    private var opcodes: ConcurrentMap<Int, Codec<*>>?
    private val opcodeTable: Array<Codec<*>?>?
    private val nextId: AtomicInteger

    init {
        require(size >= 0) { "Size cannot be less than 0!" }
        packets = ConcurrentHashMap()
        if (size == 0) {
            opcodes = ConcurrentHashMap()
            opcodeTable = null
        } else {
            opcodeTable = arrayOfNulls(size)
            opcodes = null
        }
        nextId = AtomicInteger(0)
    }

    @Throws(InstantiationException::class, IllegalAccessException::class, InvocationTargetException::class)
    @JvmOverloads
    fun <M : Packet> bind(packetClazz: Class<M>, codec: Codec<M>, opcode: Int? = null): CodecRegistration {
        @Suppress("NAME_SHADOWING")
        var opcode = opcode
        var reg = packets[packetClazz]

        if (reg != null) {
            return reg
        }

        if (opcode != null) {
            require(opcode >= 0) { "Opcode must either be null or greater than or equal to 0!" }
        } else {
            var id: Int
            try {
                do {
                    id = nextId.getAndIncrement()
                } while (get(id) != null)
            } catch (e: IndexOutOfBoundsException) {
                throw IllegalStateException("Ran out of Ids!", e)
            }
            opcode = id
        }

        val previous = get(opcode)
        check(previous == null || previous.javaClass == codec) {
            "Trying to bind an opcode where one already exists. New: " + codec.javaClass.simpleName + " Old: " + previous?.javaClass?.simpleName
        }
        put(opcode, codec)
        reg = CodecRegistration(opcode, codec)
        packets[packetClazz] = reg
        return reg
    }

    operator fun get(opcode: Int): Codec<*>? {
        val opcodes = opcodes
        return if (opcodeTable != null && opcodes == null) {
            opcodeTable[opcode]
        } else if (opcodes != null && opcodeTable == null) {
            opcodes[opcode]
        } else {
            throw IllegalStateException("One and only one codec storage system must be in use!")
        }
    }

    private fun put(opcode: Int, codec: Codec<*>) {
        val opcodes = opcodes
        if (opcodeTable != null && opcodes == null) {
            opcodeTable[opcode] = codec
        } else if (opcodes != null && opcodeTable == null) {
            opcodes[opcode] = codec
        } else {
            throw IllegalStateException("One and only one codec storage system must be in use!")
        }
    }

    @Throws(IllegalOpcodeException::class)
    fun find(opcode: Int): Codec<*> = get(opcode) ?: throw IllegalOpcodeException("Opcode $opcode is not bound!")

    fun <M : Packet> find(clazz: Class<M>): CodecRegistration = packets[clazz]!!

    override fun toString(): String = "CodecLookupService{messages=$packets, opcodes=$opcodes}"
}