package repository.impl

import com.nulabinc.backlog4j.Issue.PriorityType
import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.{Issue, Project, ResponseList}
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import entity.BacklogAuthInfoEntity
import repository.BacklogRepository
import repository.client.BacklogClientInitializer

import javax.inject.Inject

case class BacklogRepositoryImpl @Inject() (backlogClient: BacklogClientInitializer) extends BacklogRepository {

  private def createIssueParams: ViewSubmissionRequest => CreateIssueParams = (req: ViewSubmissionRequest) => {
    new CreateIssueParams(
      req.getPayload.getView.getState.getValues.get("pjId").get("acId").getSelectedOption.getValue,
      req.getPayload.getView.getState.getValues.get("ipId").get("acId").getValue
      ,1273155 // TODO: issueTypeを検討する
      ,PriorityType.Normal
    )
  }

  override def createIssue: (ViewSubmissionRequest, BacklogAuthInfoEntity) => Issue
    = (r: ViewSubmissionRequest, authInfo: BacklogAuthInfoEntity)
      => backlogClient.initialize(authInfo).createIssue(createIssueParams(r))

  override def getProjects: BacklogAuthInfoEntity => ResponseList[Project] = (authInfo: BacklogAuthInfoEntity)
    => backlogClient.initialize(authInfo).getProjects
}
