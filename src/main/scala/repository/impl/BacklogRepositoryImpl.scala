package repository.impl

import com.nulabinc.backlog4j.Issue
import com.nulabinc.backlog4j.Issue.PriorityType
import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import entity.BacklogAuthInfoEntity
import repository.BacklogRepository
import repository.client.BacklogClientInitializer

import javax.inject.Inject

case class BacklogRepositoryImpl @Inject() (backlogClient: BacklogClientInitializer) extends BacklogRepository {

  private def createIssueParams: MessageShortcutRequest => CreateIssueParams = (r: MessageShortcutRequest) => {
    // TODO: リクエストの値を設定する様に変更
    new CreateIssueParams("260625", "タイトル", 1273155, PriorityType.Normal)
  }

  override def createIssue: (MessageShortcutRequest, BacklogAuthInfoEntity) => Issue
    = (r: MessageShortcutRequest, backlogAuthInfo: BacklogAuthInfoEntity)
      => backlogClient.initialize(backlogAuthInfo).createIssue(createIssueParams(r))
}
