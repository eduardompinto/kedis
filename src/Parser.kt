private const val SIMPLE_STRINGS = '+'
private const val INTEGERS = ':'
private const val ERRORS = '-'
private const val BULK_STRINGS = '$'
private const val ARRAYS = '*'
private const val CRLF = "\r\n"

class Command(
    private vararg val args: String
) : Iterable<String> {

    private fun chunks() = sequence {
        yield("${ARRAYS}${args.size}${CRLF}")
        args.forEach { arg ->
            yield("${BULK_STRINGS}${arg.length}${CRLF}")
            yield("${arg}${CRLF}")
        }
    }

    override fun iterator() = chunks().iterator()
}

data class Response(
    val text: String? = null,
    val integer: Int? = null,
    val errorCode: String? = null
)

fun parse(msg: String): Response {
    val msgType = msg[0]
    val msgBody = msg.substring(1)

    return when (msgType) {
        ARRAYS -> throw NotImplementedError("No implementation found for arrays msg type")
        BULK_STRINGS -> when {
            msgBody.substring(0, 2) == "-1" -> Response()
            else -> Response(text = msgBody.substring(1))
        }
        ERRORS -> Response(errorCode = msgBody.substring(0, 1), text = msgBody.substring(1))
        INTEGERS -> Response(integer = msgBody.toInt())
        SIMPLE_STRINGS -> Response(text = msgBody.trim())
        else -> throw RuntimeException("Invalid Response [$msg]")
    }
}

