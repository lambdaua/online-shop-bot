package database

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.*

class PostStages(database: MongoDatabase) {
    private val collection: MongoCollection<PostStageMeta> = database.getCollection<PostStageMeta>("post-stages")

    fun add(userId: String, productId: Int, messageId: Int, key: String, value: String) {
        collection.insertOne(PostStageMeta(messageId, productId, userId, key, value))
    }

    fun getUserProductMeta(userId: String, productId: Int, messageId: Int): List<PostStageMeta> {
        return collection.find(
            combine(
                PostStageMeta::userId eq userId,
                PostStageMeta::messageId eq messageId,
                PostStageMeta::productId eq productId
            )
        ).toMutableList()
    }

    fun deletePostStages(ids: List<Id<PostStageMeta>>) {
        collection.deleteMany(PostStageMeta::id `in` ids)
    }
}

data class PostStageMeta(
    val messageId: Int,
    val productId: Int,
    val userId: String,
    val key: String,
    val value: String,
    @BsonId val id: Id<PostStageMeta> = newId()
)