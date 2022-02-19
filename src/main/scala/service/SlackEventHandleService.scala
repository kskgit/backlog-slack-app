package service

import com.slack.api.bolt.handler.builtin.{BlockActionHandler, MessageShortcutHandler}

// TODO: SlackEventHandleServiceとかにしちゃう？
trait SlackEventHandleService {
  def createIssueMessageShortcutHandler: MessageShortcutHandler
  def submitIssueBlockActionHandler: BlockActionHandler
}
