import com.slack.api.bolt.App
import com.slack.api.bolt.handler.builtin.{GlobalShortcutHandler, MessageShortcutHandler, SlashCommandHandler}
import com.slack.api.bolt.socket_mode.SocketModeApp
import org.glassfish.grizzly.http.server.util.Globals

object Main {
  def main(args: Array[String]): Unit = {
    val app = new App()

    val messageShortcutHandler: MessageShortcutHandler = (req, ctx) => {
      println("message")
      println(req)
      ctx.ack()
    }
    app.messageShortcut("create-issue-to-backlog", messageShortcutHandler)

    new SocketModeApp(app).start()
  }
}