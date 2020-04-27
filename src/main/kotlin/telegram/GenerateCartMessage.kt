package telegram

import database.CartGroupingKey

fun generateCartMessage(cart: Map<CartGroupingKey, Int>): String {
    val cartMessage = StringBuilder()
    cartMessage.append("\uD83D\uDED2  Кошик  \uD83D\uDED2\n\n")

    var totalPrice = 0.0

    var counter = 1
    cart.entries.forEach {
        val product = it.key
        val quantity = it.value

        val price = product.price.toDouble()
        val productName = product.name
        val productMeta = product.meta

        totalPrice += price * quantity
        cartMessage.append("${indexMapper(counter)} $productName $quantity шт. - ${price * quantity} грн.\n")
        productMeta.entries.forEach {
            cartMessage.append("      _${it.key}: _${it.value}\n")
        }
        counter += 1
    }

    cartMessage.append("\nЗагальна сума - $totalPrice грн.")

    return cartMessage.toString()
}

fun indexMapper(index: Int): String {
    return when (index) {
        1 -> "1️⃣"
        2 -> "2️⃣"
        3 -> "3️⃣"
        4 -> "4️⃣"
        5 -> "5️⃣"
        6 -> "6️⃣"
        7 -> "7️⃣"
        8 -> "8️⃣"
        9 -> "9️⃣"
        10 -> "\uD83D\uDD1F"
        else -> ""
    }
}

