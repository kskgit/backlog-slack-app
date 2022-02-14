import com.slack.api.bolt.App
import com.slack.api.bolt.handler.builtin.MessageShortcutHandler
import com.slack.api.bolt.socket_mode.SocketModeApp

object Main {
  def main(args: Array[String]): Unit = {
    val app = new App()

    val messageShortcutHandler: MessageShortcutHandler = (req, ctx) => {
      println("message")
      println(req)
      println(BacklogRepository.getUsers)
      ctx.ack()
    }
    app.messageShortcut("create-issue-to-backlog", messageShortcutHandler)

    new SocketModeApp(app).start()
  }
}