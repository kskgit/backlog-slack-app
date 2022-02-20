package repository

import com.nulabinc.backlog4j.{Issue, Project, ResponseList}
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import entity.BacklogAuthInfoEntity

trait BacklogRepository {
  def createIssue: (ViewSubmissionRequest, BacklogAuthInfoEntity) => Issue
  def getProjects: BacklogAuthInfoEntity => ResponseList[Project]
}
