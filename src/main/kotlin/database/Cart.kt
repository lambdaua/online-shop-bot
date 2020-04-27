package database

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.*

class Cart(database: MongoDatabase) {
    private val collection: MongoCollection<CartItem> = database.getCollection<CartItem>("cart")

    fun add(userId: String, productId: Int, name: String, price: String, meta: Map<String, String>): CartItem {
        val cartItem = CartItem(userId, productId, name, price, meta)
        collection.insertOne(cartItem)
        return cartItem
    }

    fun clearCart(userId: String) {
        collection.deleteMany(CartItem::userId eq userId)
    }

    fun forUser(userId: String): Map<CartGroupingKey, Int> {
        return collection.find(CartItem::userId eq userId).toMutableList()
            .groupingBy { CartGroupingKey(it.userId, it.productId, it.name, it.price, it.meta) }
            .eachCount()
    }
}

data class CartItem(
    val userId: String,
    val productId: Int,
    val name: String,
    val price: String,
    val meta: Map<String, String>,
    @BsonId val id: Id<CartItem> = newId()
)

data class CartGroupingKey(
    val userId: String,
    val productId: Int,
    val name: String,
    val price: String,
    val meta: Map<String, String>
)