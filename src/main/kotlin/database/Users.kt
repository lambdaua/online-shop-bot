package database

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.telegram.telegrambots.api.objects.Location
import java.util.*


class Users(database: MongoDatabase) {
    private val collection: MongoCollection<User> = database.getCollection<User>("customers")

    fun create(id: Long, passport: Passport): User {
        val user = User(userId = id, passport = passport)
        collection.insertOne(user)
        return user
    }

    fun getById(id: String): User? {
        return collection.findOneById(ObjectId(id))
    }

    fun getByTelegramId(userId: Long): User? {
        return collection.findOne(User::userId eq userId)
    }

    fun didPurchase(id: Id<User>, purchase: Purchase) {
        collection.updateOne(User::id eq id, Updates.set("purchase", purchase))
    }

    fun didBlockBot(id: Long) {
        collection.updateOne(User::userId eq id, set(User::hasBlockedBot, true))
    }

    fun didUnblockBot(id: Long) {
        collection.updateOne(User::userId eq id, set(User::hasBlockedBot, false))
    }

    fun utmSource(id: Long, source: String) {
        collection.updateOne(
            Filters.and(User::userId eq id),
            Updates.addToSet(User::sources.name, source)
        )
    }

    fun aggregateAdminStats(): AdminStats {
        val instance = Calendar.getInstance()
        instance.add(Calendar.HOUR, -24)
        val dayBefore = ObjectId(instance.time)

        val lastDayStat = collection.find(User::id gt dayBefore).count()
        val allStat = collection.countDocuments().toInt()
        return AdminStats(lastDayStat, allStat)
    }

    fun listAll(): List<User> {
        return collection.find().toMutableList()
    }

    fun addContact(userId: Long, contact: String) {
        collection.updateOne(User::userId eq userId, set(User::passport / Passport::tlContact, contact))
    }

    fun addLocation(userId: Long, location: Location) {
        collection.updateOne(User::userId eq userId, set(User::location, location))
    }

    fun setPending(userId: Long) {
        collection.updateOne(User::userId eq userId, set(User::pending, true))
    }

    fun deletePending(userId: Long) {
        collection.updateOne(User::userId eq userId, set(User::pending, false))
    }

    fun addStage(id: Long, stage: String) {
        collection.updateOne(User::userId eq id, addToSet(User::stages, stage))
    }

    fun clearStages(id: Long) {
        collection.updateOne(User::userId eq id, User::stages setTo listOf())
    }
}

data class User(
    @BsonId val id: Id<User> = newId(),
    val userId: Long? = null,
    val passport: Passport? = null,
    val sources: List<String> = listOf(),
    val deliveryInfo: DeliveryInfo? = null,
    val location: Location? = null,
    val purchase: Purchase? = null,
    val hasBlockedBot: Boolean = false,
    val pending: Boolean = false,
    val stages: List<String> = listOf()
)

data class Passport(
    val tlFirstName: String, val tlLastName: String, val tlUsername: String,
    val tlLanguageCode: String, val tlLanguageCodes: List<String>, val tlContact: String? = ""
)

data class Purchase(
    val date: Date? = null,
    val updateUrl: String? = "",
    val duration: Int? = null
)

data class DeliveryInfo(
    val name: String,
    val surname: String,
    val city: String,
    val postNumber: Int
)

data class AdminStats(val lastDay: Int, val all: Int)