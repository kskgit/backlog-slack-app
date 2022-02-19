package endpoint.impl

import com.slack.api.bolt.{App, AppConfig}
import com.slack.api.bolt.socket_mode.SocketModeApp
import endpoint.EndPoint
import service.SlackEventHandleService

import javax.inject.Inject

case class EndPointSocketMode @Inject()(slackEventHandleService: SlackEventHandleService) extends EndPoint {
  override def startServer(): Unit = {
    val config = new AppConfig
    config.setSingleTeamBotToken(sys.env("SLACK_BOT_TOKEN"))
    config.setSigningSecret(sys.env("SLACK_SIGNING_SECRET"))

    val app = new App(config)
    app.messageShortcut("create-issue-to-backlog", slackEventHandleService.createIssueMessageShortcutHandler)
    app.blockAction("submit-issue-info", slackEventHandleService.submitIssueBlockActionHandler)

    new SocketModeApp(app).start()
  }
}
