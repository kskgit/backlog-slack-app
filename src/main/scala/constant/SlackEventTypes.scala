package constant

sealed abstract class SlackEventTypes(val typeName: String)

/** イベントタイプを保持する */
object SlackEventTypes {
  case object AcceptCreateIssueRequest
      extends SlackEventTypes("accept-create-issue-request")
  case object RegistrationAuthInfoToStore
      extends SlackEventTypes("registration-auth-info-to-store")
  case object PostIssueInfoReqToSlack
      extends SlackEventTypes("post-issue-info-req-to-slack")
  case object RegistrationIssueToBacklog
      extends SlackEventTypes("registration-issue-to-backlog")
}
