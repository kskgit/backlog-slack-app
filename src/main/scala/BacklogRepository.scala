import com.nulabinc.backlog4j.Issue.PriorityType
import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.{BacklogClient, BacklogClientFactory, Issue}
import com.nulabinc.backlog4j.conf.{BacklogComConfigure, BacklogConfigure}
import com.slack.api.bolt.request.builtin.MessageShortcutRequest

// todo interfaceを作成する
object BacklogRepository {
  private val configure: BacklogConfigure = new BacklogComConfigure(sys.env("BACKLOG_SPACE_ID")).apiKey(sys.env("BACKLOG_API_KEY"))
  private val backlog: BacklogClient = new BacklogClientFactory(configure).newClient

  // TODO: パラメータを設定する処理を記述する

  private def createIssueParams: MessageShortcutRequest => CreateIssueParams = (r: MessageShortcutRequest) => {
    new CreateIssueParams("260625", "タイトル", 1273155, PriorityType.Normal)
  }

  def createIssue: MessageShortcutRequest => Issue = (r: MessageShortcutRequest) =>backlog.createIssue(createIssueParams(r))
}
