package repository

import com.slack.api.bolt.handler.builtin.{BlockActionHandler, MessageShortcutHandler}

trait SlackRepository {
  def postInputIssueInfoRequest:MessageShortcutHandler
  def postBacklogAuthInfoRequest:MessageShortcutHandler
  def registrationIssueToBacklog:BlockActionHandler
}
