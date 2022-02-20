package repository.impl

import com.nulabinc.backlog4j.Issue.PriorityType
import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.{Project, ResponseList}
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

  override def createIssue: (ViewSubmissionRequest, BacklogAuthInfoEntity) => String
    = (r: ViewSubmissionRequest, authInfo: BacklogAuthInfoEntity) => {
          val backlogClientAuthed = backlogClient.initialize(authInfo)
          val issue = backlogClientAuthed.createIssue(createIssueParams(r))
          backlogClientAuthed.getIssueUrl(issue)
      }

  override def getProjects: BacklogAuthInfoEntity => ResponseList[Project] = (authInfo: BacklogAuthInfoEntity)
    => backlogClient.initialize(authInfo).getProjects
}
