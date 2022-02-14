package service

import com.slack.api.bolt.handler.builtin.MessageShortcutHandler

trait MessageShortcutHandleService {
  def createIssueMessageShortcutHandler: MessageShortcutHandler
}
