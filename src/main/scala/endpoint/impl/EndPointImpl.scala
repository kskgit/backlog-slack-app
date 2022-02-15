package endpoint.impl

import com.slack.api.bolt.App
import com.slack.api.bolt.socket_mode.SocketModeApp
import endpoint.EndPoint
import service.MessageShortcutHandleService

import javax.inject.Inject

case class EndPointImpl @Inject()(messageShortcutHandleService: MessageShortcutHandleService) extends EndPoint {
  override def startServer(): Unit = {
    val app = new App()
    app.messageShortcut("create-issue-to-backlog", messageShortcutHandleService.createIssueMessageShortcutHandler)
    new SocketModeApp(app).start()
  }
}
