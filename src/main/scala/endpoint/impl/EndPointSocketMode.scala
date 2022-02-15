package endpoint.impl

import com.slack.api.bolt.App
import com.slack.api.bolt.socket_mode.SocketModeApp
import endpoint.EndPoint
import service.SlackEventHandleService

import javax.inject.Inject

case class EndPointSocketMode @Inject()(slackEventHandleService: SlackEventHandleService) extends EndPoint {
  override def startServer(): Unit = {
    val app = new App()
    app.messageShortcut("create-issue-to-backlog", slackEventHandleService.createIssueMessageShortcutHandler)
    new SocketModeApp(app).start()
  }
}
