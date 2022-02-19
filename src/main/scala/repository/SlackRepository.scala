package repository

import com.slack.api.bolt.handler.builtin.MessageShortcutHandler

trait SlackRepository {
  def postInputIssueInfoRequest:MessageShortcutHandler
  def postBacklogAuthInfoRequest:MessageShortcutHandler
}
