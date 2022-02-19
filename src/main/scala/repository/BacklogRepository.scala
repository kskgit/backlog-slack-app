package repository

import com.nulabinc.backlog4j.Issue
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import entity.BacklogAuthInfoEntity

trait BacklogRepository {
  def createIssue: (ViewSubmissionRequest, BacklogAuthInfoEntity) => Issue
}
