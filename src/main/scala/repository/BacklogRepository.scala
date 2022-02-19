package repository

import com.nulabinc.backlog4j.Issue
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import entity.BacklogAuthInfoEntity

trait BacklogRepository {
  def createIssue: (MessageShortcutRequest, BacklogAuthInfoEntity) => Issue
}
