package service

import com.slack.api.bolt.handler.builtin.MessageShortcutHandler

// TODO: SlackEventHandleServiceとかにしちゃう？
trait SlackEventHandleService {
  def createIssueMessageShortcutHandler: MessageShortcutHandler
}
