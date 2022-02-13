import com.slack.api.bolt.App
import com.slack.api.bolt.handler.builtin.SlashCommandHandler
import com.slack.api.bolt.socket_mode.SocketModeApp

object Main {
  def main(args: Array[String]): Unit = {
    val app = new App()
    val slashCommandHandler: SlashCommandHandler = (req, ctx) => {
      ctx.ack(":wave: Hello!")
    }
    app.command("/hello", slashCommandHandler)
    new SocketModeApp(app).start()
  }
}