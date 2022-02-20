package service

import com.slack.api.bolt.handler.builtin.{BlockActionHandler, MessageShortcutHandler, ViewSubmissionHandler}

trait SlackEventHandleService {
  def acceptCreateIssueRequest: MessageShortcutHandler
  def registrationAuthInfoToStore: ViewSubmissionHandler
  def registrationIssueToBacklog: ViewSubmissionHandler
}
