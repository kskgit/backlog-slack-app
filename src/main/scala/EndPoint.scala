import com.slack.api.bolt.App
import com.slack.api.bolt.socket_mode.SocketModeApp
import service.MessageShortcutHandleService

import javax.inject.Inject

case class EndPoint @Inject() (messageShortcutHandleService:MessageShortcutHandleService) {
  def startSocketModeApp():Unit = {
    val app = new App()
    app.messageShortcut("create-issue-to-backlog", messageShortcutHandleService.createIssueMessageShortcutHandler)
    new SocketModeApp(app).start()
  }
}