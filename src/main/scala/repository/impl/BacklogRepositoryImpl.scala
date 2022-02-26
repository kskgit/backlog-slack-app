package repository.impl

import com.nulabinc.backlog4j.Issue.PriorityType
import com.nulabinc.backlog4j.api.option.{CreateIssueParams, GetIssuesParams}
import com.nulabinc.backlog4j.{Issue, IssueType, Project, ResponseList}
import params.BacklogAuthInfoParams
import repository.BacklogRepository
import repository.client.BacklogClientInitializer

import javax.inject.Inject

case class BacklogRepositoryImpl @Inject() (
    backlogClient: BacklogClientInitializer
) extends BacklogRepository {

  // TODO: ViewSubmissionRequest を受け取る場合storeRepositoryと平仄を合わせる
  override def getCreateIssueParams
      : (String, String, Int, String) => CreateIssueParams =
    (projectId, issueTitle, issueTypeId, messageLink) => {
      new CreateIssueParams(
        projectId,
        issueTitle,
        issueTypeId,
        PriorityType.Normal
      ).description(messageLink)
    }

  override def createIssue
      : (CreateIssueParams, BacklogAuthInfoParams) => String =
    (createIssueParams: CreateIssueParams, authInfo: BacklogAuthInfoParams) => {
      val backlogClientAuthed = backlogClient.initialize(authInfo)
      val issue = backlogClientAuthed.createIssue(createIssueParams)
      backlogClientAuthed.getIssueUrl(issue)
    }

  override def getProjects: BacklogAuthInfoParams => ResponseList[Project] =
    (authInfo: BacklogAuthInfoParams) =>
      backlogClient.initialize(authInfo).getProjects

  override def getIssueTypes
      : (BacklogAuthInfoParams, String) => ResponseList[IssueType] =
    (authInfo: BacklogAuthInfoParams, projectId: String) =>
      backlogClient.initialize(authInfo).getIssueTypes(projectId)
}
