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


private fun getParser(msgType: Char): (msg: String) -> Response = when (msgType) {
    '+' -> { msg: String ->
        Response(text = msg.trim())
    }
    ':' -> { msg: String ->
        Response(integer = msg.toInt())
    }
    '-' -> { msg: String ->
        Response(errorCode = msg.substring(0, 1), text = msg.substring(1))
    }
    '$' -> { msg: String ->
        Response(text = msg.substring(1))
    }
    else -> throw RuntimeException("Invalid Response :(")
}

fun parse(msg: String): Response {
    val parser = getParser(msg[0])
    return parser.invoke(msg.substring(1))
}
