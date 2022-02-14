package repository

import com.nulabinc.backlog4j.Issue
import com.slack.api.bolt.request.builtin.MessageShortcutRequest

trait BacklogRepository {
  def createIssue: MessageShortcutRequest => Issue
}
