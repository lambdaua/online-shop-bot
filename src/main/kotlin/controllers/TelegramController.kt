package controllers

import org.telegram.telegrambots.api.objects.Update
import telegram.MessagesReceiver
import java.util.concurrent.Executors
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/tl")
@Produces(MediaType.APPLICATION_JSON)
class TelegramController(
    private val messagesReceiver: MessagesReceiver
) {

    private val executor = Executors.newFixedThreadPool(32)

    @POST
    @Path("/webhook")
    @Consumes(MediaType.APPLICATION_JSON)
    fun webhook(update: Update): Response {
//        val doc = Document.parse(mapper.writeValueAsString(update))
//        telegramLogs.insertOne(doc)
//        val updateId = doc.getObjectId("_id").toString()

        executor.submit {
            try {
                messagesReceiver.handle(update)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Response.ok(OkResponse()).build()
    }
}