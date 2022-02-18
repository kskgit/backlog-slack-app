package repository.impl

import com.nulabinc.backlog4j.Issue.PriorityType
import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.conf.{BacklogComConfigure, BacklogConfigure}
import com.nulabinc.backlog4j.{BacklogClient, BacklogClientFactory, Issue}
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import repository.BacklogRepository

case class BacklogRepositoryImpl() extends BacklogRepository {
  private val configure: BacklogConfigure = new BacklogComConfigure(sys.env("BACKLOG_SPACE_ID")).apiKey(sys.env("BACKLOG_API_KEY"))
  private val backlog: BacklogClient = new BacklogClientFactory(configure).newClient

  private def createIssueParams: MessageShortcutRequest => CreateIssueParams = (r: MessageShortcutRequest) => {
    // TODO: リクエストの値を設定する様に変更
    new CreateIssueParams("260625", "タイトル", 1273155, PriorityType.Normal)
  }

  override def createIssue: MessageShortcutRequest => Issue = (r: MessageShortcutRequest) => backlog.createIssue(createIssueParams(r))
}
