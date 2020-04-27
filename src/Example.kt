fun main() {
    RedisClient().use { cli ->
        val key = "key"
        val value = "value"
        println(cli.set(key, value))
        print(cli.get(key))
    }
}