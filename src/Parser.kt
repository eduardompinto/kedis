class Command(
    private vararg val args: String
) : Iterable<String> {

    private fun chunks() = sequence {
        yield("*${args.size}\r\n")
        args.forEach { arg ->
            yield("\$${arg.length}\r\n")
            yield("${arg}\r\n")
        }
    }

    override fun iterator() = chunks().iterator()
}

data class Response(
    val text: String? = null,
    val integer: Int? = null,
    val errorCode: String? = null
) {
    val success = errorCode == null
}


fun parse(msg: String): Response {
    val msgType = msg[0]
    val msgBody = msg.substring(1)
    return when (msgType) {
        '+' -> Response(text = msgBody.trim())
        ':' -> Response(integer = msgBody.toInt())
        '-' -> Response(errorCode = msgBody.substring(0, 1), text = msgBody.substring(1))
        '$' -> Response(text = msgBody.substring(1))
        else -> throw RuntimeException("Invalid Response [$msg]")
    }
}

