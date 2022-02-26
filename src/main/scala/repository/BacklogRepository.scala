package repository

import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.{IssueType, Project, ResponseList}
import params.BacklogAuthInfoParams

trait BacklogRepository {

  /** Backlogに保存する課題情報を取得する
    *
    * @param projectId プロジェクトID
    * @param issueTitle 課題タイトル
    * @param issueTypeId 課題タイプID
    * @param messageLink Slackの会話URL
    * @return
    */
  def getCreateIssueParams(
      projectId: String,
      issueTitle: String,
      issueTypeId: Int,
      messageLink: String
  ): CreateIssueParams

  /** Backlogに課題を保存する
    *
    * @param createIssueParams 登録する課題の情報
    * @param backlogAuthInfoParams Backlogの認証情報
    * @return 登録した課題のURL
    */
  def createIssue(
      createIssueParams: CreateIssueParams,
      backlogAuthInfoParams: BacklogAuthInfoParams
  ): String

  /** Backlogの課題一覧を取得する
    *
    * @param backlogAuthInfoParams Backlogの認証情報
    * @return 課題一覧
    */
  def getProjects(
      backlogAuthInfoParams: BacklogAuthInfoParams
  ): ResponseList[Project]

  def getIssueTypes: (BacklogAuthInfoParams, String) => ResponseList[IssueType]
}
