package endpoint.impl

import com.slack.api.bolt.jetty.SlackAppServer
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.bolt.{App, AppConfig}
import constant.SlackEventTypes
import endpoint.EndPoint
import service.SlackEventHandleService

import java.util
import javax.inject.Inject

case class EndPointImpl @Inject() (
    slackEventHandleService: SlackEventHandleService
) extends EndPoint {
  override def startServer(): Unit = {
    val config = new AppConfig
    config.setSingleTeamBotToken(sys.env("SLACK_BOT_TOKEN"))
    config.setSigningSecret(sys.env("SLACK_SIGNING_SECRET"))
    val app = new App(config)
    app.messageShortcut(
      SlackEventTypes.AcceptCreateIssueRequest.typeName,
      slackEventHandleService.acceptCreateIssueRequest
    )
    app.viewSubmission(
      SlackEventTypes.RegistrationAuthInfoToStore.typeName,
      slackEventHandleService.registrationAuthInfoToStore
    )
    app.blockAction(
      SlackEventTypes.PostIssueInfoReqToSlack.typeName,
      slackEventHandleService.postIssueInfoReqToSlack
    )
    app.viewSubmission(
      SlackEventTypes.RegistrationIssueToBacklog.typeName,
      slackEventHandleService.registrationIssueToBacklog
    )
    val oauthApp = new App().asOAuthApp(true)

    val map = new util.HashMap[String, App] {
      {
        put("/slack/events", app) // POST /slack/events (Slack API からのリクエストのみ)
        put(
          "/slack/oauth",
          oauthApp
        )
      }
    }
//    val server = new SlackAppServer(map, sys.env("PORT").toInt)
//    server.start()

    //to dev
    // TODO: 環境変数での切り替え
    new SocketModeApp(app).start()

  }
}
