import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.module.kotlin.KotlinModule
import configuration.AppConfiguration
import controllers.TelegramController
import database.Cart
import database.Orders
import database.PostStages
import database.Users
import io.dropwizard.Application
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import okhttp3.OkHttpClient
import org.litote.kmongo.KMongo
import org.litote.kmongo.id.jackson.IdJacksonModule
import org.litote.kmongo.util.KMongoConfiguration
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import retrofit2.converter.jackson.JacksonConverterFactory
import telegram.ChatBot
import telegram.LongPolling
import telegram.MessagesReceiver
import java.util.concurrent.TimeUnit

class App : Application<AppConfiguration>() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ApiContextInitializer.init()
            App().run(*args)
        }
    }

    override fun initialize(bootstrap: Bootstrap<AppConfiguration>?) {
        super.initialize(bootstrap)

        val module = KotlinModule()

        bootstrap!!.objectMapper
            .registerModule(module)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(IdJacksonModule())

        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
            bootstrap.configurationSourceProvider,
            EnvironmentVariableSubstitutor(false)
        )

        KMongoConfiguration.bsonMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        KMongoConfiguration.bsonMapperCopy.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        KMongoConfiguration.extendedJsonMapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    }

    override fun run(configuration: AppConfiguration?, environment: Environment?) = try {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        mapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.registerModule(IdJacksonModule())

        val jsonProvider = JacksonJaxbJsonProvider()
        jsonProvider.setMapper(mapper)
        environment!!.jersey().register(jsonProvider)

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val jacksonConverterFactory = JacksonConverterFactory.create(mapper)

        val mongoClient = KMongo.createClient(configuration!!.mongoUri)
        val database = mongoClient.getDatabase(configuration.mongoUri.database!!)
//        val wayForPayOrders = database.getCollectionOfName<Document>("wayforpay")

        val users = Users(database)
        val orders = Orders(database)
        val cart = Cart(database)
        val postStages = PostStages(database)

        val botName = configuration.bot.name
        val botToken = configuration.bot.token

        val chatBot = ChatBot(botToken, botName, listOf())

        val messagesReceiver =
            MessagesReceiver(chatBot, users, configuration, TemplateManager(mapper), orders, cart, postStages)

        if (configuration.isLongpoll()) {
            val botsApi = TelegramBotsApi()
            botsApi.registerBot(LongPolling(botName, botToken, messagesReceiver))
        }

        environment.jersey().register(TelegramController(messagesReceiver))
//        environment.jersey().register(PaddleController(users, chatBot, orders, configuration, wayForPayOrders))
    } catch (e: Exception) {
        e.printStackTrace()
        throw RuntimeException()
    }
}