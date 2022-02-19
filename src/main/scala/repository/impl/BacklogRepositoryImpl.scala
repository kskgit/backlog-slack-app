package repository.impl

import com.nulabinc.backlog4j.Issue
import com.nulabinc.backlog4j.Issue.PriorityType
import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import repository.BacklogRepository
import repository.client.BacklogClientInitialized

import javax.inject.Inject

case class BacklogRepositoryImpl @Inject() (backlogClient: BacklogClientInitialized) extends BacklogRepository {

  private def createIssueParams: MessageShortcutRequest => CreateIssueParams = (r: MessageShortcutRequest) => {
    // TODO: リクエストの値を設定する様に変更
    new CreateIssueParams("260625", "タイトル", 1273155, PriorityType.Normal)
  }

  override def createIssue: MessageShortcutRequest => Issue = (r: MessageShortcutRequest) => backlogClient.backlog.createIssue(createIssueParams(r))
}
