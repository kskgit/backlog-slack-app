package repository.impl

import com.nulabinc.backlog4j.Issue.PriorityType
import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.{Project, ResponseList}
import params.BacklogAuthInfoParams
import repository.BacklogRepository
import repository.client.BacklogClientImpl

import javax.inject.Inject

case class BacklogRepositoryImpl @Inject() (
    backlogClient: BacklogClientImpl
) extends BacklogRepository {

  override def getCreateIssueParams(
      projectId: String,
      issueTitle: String,
      issueTypeId: Int,
      messageLink: String
  ): CreateIssueParams =
    new CreateIssueParams(
      projectId,
      issueTitle,
      issueTypeId,
      PriorityType.Normal
    ).description(messageLink)

  override def createIssue(
      createIssueParams: CreateIssueParams,
      backlogAuthInfoParams: BacklogAuthInfoParams
  ): String =
    backlogClient.createIssue(createIssueParams, backlogAuthInfoParams)

  override def getProjects(
      authInfo: BacklogAuthInfoParams
  ): ResponseList[Project] =
    backlogClient.getProjects(authInfo)

  override def getIssueTypes(
      authInfo: BacklogAuthInfoParams,
      projectId: String
  ) =
    backlogClient.getIssueTypes(authInfo, projectId)
}
