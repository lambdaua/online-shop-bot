package database

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId

class Orders(database: MongoDatabase) {
    private val collection: MongoCollection<Order> = database.getCollection<Order>("orders")

    fun create(userId: String, cartItem: List<CartItem>): Id<Order> {
        val order = Order(userId = userId, cart = cartItem)
        collection.insertOne(order)
        return order.id
    }

    fun didPurchase(
        orderId: String
    ): Order {
        val toId = ObjectId(orderId).toId<Order>()
        collection.updateOne(
            Order::id eq toId,
            set(Order::status, OrderStatus.paid)
        )
        return getById(orderId)!!
    }

    fun failed(
        orderId: Id<Order>
    ) {
        collection.updateOne(
            Order::id eq orderId,
            set(Order::status, OrderStatus.failed)
        )
    }

    fun getById(id: String): Order? {
        return collection.findOneById(ObjectId(id))
    }
}

data class Order(
    @BsonId val id: Id<Order> = newId(),
    val userId: String,
    val cart: List<CartItem> = listOf(),
    val status: OrderStatus? = OrderStatus.pending
)

enum class OrderStatus { paid, pending, failed }
