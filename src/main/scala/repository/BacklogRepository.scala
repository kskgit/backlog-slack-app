package repository

import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.{IssueType, Project, ResponseList}
import params.BacklogAuthInfoParams

trait BacklogRepository {
  def getCreateIssueParams: (String, String, Int, String) => CreateIssueParams
  def createIssue: (CreateIssueParams, BacklogAuthInfoParams) => String
  def getProjects: BacklogAuthInfoParams => ResponseList[Project]
  def getIssueTypes: (BacklogAuthInfoParams, String) => ResponseList[IssueType]
}
