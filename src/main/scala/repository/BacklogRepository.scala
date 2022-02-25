package repository

import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.{IssueType, Project, ResponseList}
import entity.BacklogAuthInfoEntity

trait BacklogRepository {
  def getCreateIssueParams: (String, String, Int, String) => CreateIssueParams
  def createIssue: (CreateIssueParams, BacklogAuthInfoEntity) => String
  def getProjects: BacklogAuthInfoEntity => ResponseList[Project]
  def getIssueTypes: (BacklogAuthInfoEntity, String) => ResponseList[IssueType]
}
