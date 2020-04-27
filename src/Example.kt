fun main() {
    RedisClient().use { cli ->
        val key = "key"
        val value = "value"
        println(cli.set(key, value))
        println(cli.get(key))
        println(cli.get("batata"))
    }
}