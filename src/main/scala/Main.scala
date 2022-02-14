import com.slack.api.bolt.App
import com.slack.api.bolt.handler.builtin.MessageShortcutHandler
import com.slack.api.bolt.socket_mode.SocketModeApp

object Main {
  def main(args: Array[String]): Unit = {
    val app = new App()

    val createIssueMessageShortcutHandler: MessageShortcutHandler = (req, ctx) => {
      // TODO: DBからユーザーのプロジェクトを取得
      //  取得出来ればそれを設定したブロックを返す
      //  取得不可であれば設定用のブロックを返す
      BacklogRepository.createIssue(req)
      ctx.ack()
    }
    app.messageShortcut("create-issue-to-backlog", createIssueMessageShortcutHandler)

    new SocketModeApp(app).start()
  }
}