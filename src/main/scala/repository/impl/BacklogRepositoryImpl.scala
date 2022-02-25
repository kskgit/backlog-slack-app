package repository.impl

import com.nulabinc.backlog4j.Issue.PriorityType
import com.nulabinc.backlog4j.api.option.{CreateIssueParams, GetIssuesParams}
import com.nulabinc.backlog4j.{Issue, IssueType, Project, ResponseList}
import entity.BacklogAuthInfoEntity
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
      : (CreateIssueParams, BacklogAuthInfoEntity) => String =
    (createIssueParams: CreateIssueParams, authInfo: BacklogAuthInfoEntity) => {
      val backlogClientAuthed = backlogClient.initialize(authInfo)
      val issue = backlogClientAuthed.createIssue(createIssueParams)
      backlogClientAuthed.getIssueUrl(issue)
    }

  override def getProjects: BacklogAuthInfoEntity => ResponseList[Project] =
    (authInfo: BacklogAuthInfoEntity) =>
      backlogClient.initialize(authInfo).getProjects

  override def getIssueTypes
      : (BacklogAuthInfoEntity, String) => ResponseList[IssueType] =
    (authInfo: BacklogAuthInfoEntity, projectId: String) =>
      backlogClient.initialize(authInfo).getIssueTypes(projectId)
}
