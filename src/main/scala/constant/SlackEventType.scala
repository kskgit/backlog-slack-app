package constant

enum SlackEventType(val typeName: String) :
  case AcceptCreateIssueRequest extends SlackEventType("accept-create-issue-request")
  case RegistrationAuthInfoToStore extends SlackEventType("registration-auth-info-to-store")
  case PostIssueInfoReqToSlack extends SlackEventType("post-issue-info-req-to-slack")
  case RegistrationIssueToBacklog extends SlackEventType("registration-issue-to-backlog")

