package service

import com.slack.api.bolt.handler.builtin.{BlockActionHandler, MessageShortcutHandler}

trait SlackEventHandleService {
  def acceptCreateIssueRequest: MessageShortcutHandler
  def registrationAuthInfoToStore: BlockActionHandler
  def registrationIssueToBacklog: BlockActionHandler
}
