package service

import com.slack.api.bolt.handler.BoltEventHandler
import com.slack.api.bolt.handler.builtin.MessageShortcutHandler
import com.slack.api.model.event.{MessageChangedEvent, MessageEvent}

// TODO: SlackEventHandleServiceとかにしちゃう？
trait SlackEventHandleService {
  def createIssueMessageShortcutHandler: MessageShortcutHandler
}
