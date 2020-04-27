//package controllers
//
//import com.fasterxml.jackson.databind.JsonNode
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.mongodb.client.MongoCollection
//import conf.AppConfiguration
//import configuration.AppConfiguration
//import database.OrderStatus
//import database.Orders
//import database.Users
//import org.bson.Document
//import telegram.ChatBot
//import telegram.Peer
//import telegram.handlers.OrderHandler
//import utils.SecurityHelper
//import java.math.BigDecimal
//import java.net.URI
//import javax.ws.rs.*
//import javax.ws.rs.core.MediaType
//import javax.ws.rs.core.Response
//
//@Path("/wfp")
//class PaddleController(
//    val users: Users,
//    val bot: ChatBot,
//    val orders: Orders,
//    val conf: AppConfiguration,
//    val wayForPayOrders: MongoCollection<Document>
//) {
//    @POST
//    @Path("callback")
//    fun callback(
//        wayForPayRequest: String
//    ): Response {
//        val mapper = ObjectMapper()
//        val jsonNode = mapper.readTree(wayForPayRequest)
//
//        wayForPayOrders.insertOne(Document.parse(wayForPayRequest))
//
//        try {
//            val transactionStatus = jsonNode["transactionStatus"].asText()
//            if (transactionStatus == "Approved") {
//                val orderId = jsonNode["orderReference"].asText()
//
//                //ToDo класть все от вєйфорпей
//                val order = orders.getById(orderId)!!
//                if (order.status != OrderStatus.pending) {
//                    return Response.ok(
//                        mapper.writeValueAsString(
//                            WayForPayResponse.create(
//                                jsonNode["orderReference"].asText(),
//                                "accept",
//                                System.currentTimeMillis() / 1000,
////                                conf.wfpSecret
//                            )
//                        )
//                    ).build()
//                }
//                orders.didPurchase(orderId)
//                val user = users.getById(order.userId)!!
//
//                val cartMessage = OrderHandler.generateFinalMessage(order.cart)
//                bot.sendOrderToWhiteAdminGroup(user, cartMessage, jsonNode["amount"].asText())
//                Peer(bot, user.userId!!.toString(), conf).sendText(
//                    "Чудово! Оплата пройшла успішно\n\n$cartMessage\n\nКоли кава буде готова мы Вам напишемо\n\n" +
//                            "З будь-яких питань пишіть у підтримку або тисніть /help"
//                )
//                users.clearCart(user.userId)
//
//                bot.sendAdminAboutUser("purchase was successful", user)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            bot.sendAdmin("purchase FAIL: purchase on web wayforpay")
//        }
//
//
//        val writeValueAsString = mapper.writeValueAsString(
//            WayForPayResponse.create(
//                jsonNode["orderReference"].asText(),
//                "accept",
//                System.currentTimeMillis() / 1000,
//                conf.wfpSecret
//            )
//        )
//        print(writeValueAsString)
//        return Response.ok(
//            writeValueAsString
//        ).build()
//    }
//
//    @GET
//    @Path("/pay-now")
//    fun payNow(
//        @QueryParam("payload") payload: String
//    ): Response {
//        val user = users.getById(payload)!!
//        if (user.cart.sumBy { it.quantity * it.priceInCents } == 0) {
//            bot.sendAdminAboutUser("Нажал оплату, когда заказ 0 грн. Зачем?", user)
//
//            Peer(
//                bot,
//                user.userId!!.toString(),
//                conf
//            ).sendText("Для початку треба щось вибрати \uD83D\uDE44\uD83D\uDE46")
//
//            return Response.temporaryRedirect(URI("https://t.me/${conf.bot.name}")).build()
//        }
//        val orderId = orders.create(payload, user.cart)
//
//        val redirectUrl = try {
//            WayForPayClient(conf = conf).redirectLink(orderId.toString(), user.cart)
//        } catch (e: Exception) {
//            orders.failed(orderId)
//            "https://t.me/${conf.bot.name}"
//        }
//        return Response.temporaryRedirect(URI(redirectUrl)).build()
//    }
//}
//
//data class WayForPayResponse(
//    val orderReference: String,
//    val status: String,
//    val time: Long,
//    val signature: String
//) {
//    companion object {
//        fun create(
//            orderReference: String,
//            status: String,
//            time: Long,
//            privateKey: String
//        ): WayForPayResponse {
//            val stringToSign = StringBuilder()
//            stringToSign.append(orderReference)
//            stringToSign.append(";")
//            stringToSign.append(status)
//            stringToSign.append(";")
//            stringToSign.append(time)
//
//            val hmacmD5 = SecurityHelper().getHmacMD5(privateKey, stringToSign.toString(), "HmacMD5")
//            return WayForPayResponse(orderReference, status, time, hmacmD5)
//        }
//    }
//}