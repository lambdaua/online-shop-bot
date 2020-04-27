package configuration

import com.mongodb.MongoClientURI
import io.dropwizard.Configuration

class AppConfiguration(
    val mongoUri: MongoClientURI,
    val bot: Bot,
    val serviceUrl: String
) : Configuration() {
    private val longpoll = true

    fun isLongpoll(): Boolean {
        return longpoll
    }
}

data class Bot(var name: String, var token: String)