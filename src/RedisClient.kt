import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.Closeable
import java.io.InputStreamReader
import java.net.Socket
import java.net.SocketTimeoutException


class RedisClient(
    ip: String = "localhost",
    port: Int = 6379
) : Closeable {

    private val socket = Socket(ip, port).apply {
        this.soTimeout = 100 // milliseconds
    }
    private val out = BufferedOutputStream(socket.getOutputStream())
    private val input = BufferedReader(InputStreamReader(socket.getInputStream()))

    fun set(key: String, value: String): Boolean {
        sendData(Command("SET", key, value))
        val response = parse(readData())
        return response.errorCode == null
    }

    fun get(key: String): String? {
        sendData(Command("GET", key))
        val response = parse(readData())
        if (response.errorCode == null) {
            return response.text
        }
        throw RuntimeException("Not successful GET command [${response.errorCode}]")
    }

    private fun sendData(cmd: Command) {
        cmd.forEach {
            out.write(it.toByteArray())
        }
        out.flush()
    }

    private fun readData(): String {
        val lines = StringBuilder()
        try {
            while (true) {
                val line = input.readLine()
                if (line.isBlank()) {
                    return lines.toString()
                }
                lines.append(line)
            }
        } catch (exception: SocketTimeoutException) {
            // TODO: using the timeout here to get the response is wrong.
            return lines.toString()
        }
    }

    override fun close() {
        out.close()
        input.close()
        socket.close()
    }


}