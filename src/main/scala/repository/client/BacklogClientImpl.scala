package repository.client

import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.{
  BacklogClient,
  BacklogClientFactory,
  IssueType,
  Project,
  ResponseList
}
import com.nulabinc.backlog4j.conf.{BacklogComConfigure, BacklogConfigure}
import params.BacklogAuthInfoParams

/** Backlogクライアントライブラリのメソッドをラップして提供する */
case class BacklogClientImpl() {

  /** Backlogに課題を保存する
    *
    * @param createIssueParams 登録する課題の情報
    * @param backlogAuthInfoParams Backlogの認証情報
    * @return 登録した課題のURL
    */
  def createIssue(
      createIssueParams: CreateIssueParams,
      backlogAuthInfoParams: BacklogAuthInfoParams
  ): String = {
    val backlogClientAuthed = initialize(backlogAuthInfoParams)
    val issue = backlogClientAuthed.createIssue(createIssueParams)
    backlogClientAuthed.getIssueUrl(issue)
  }

  /** Backlogのプロジェクト一覧を取得する
    *
    * @param backlogAuthInfoParams Backlogの認証情報
    * @return 課題一覧
    */
  def getProjects(
      backlogAuthInfoParams: BacklogAuthInfoParams
  ): ResponseList[Project] = {
    initialize(backlogAuthInfoParams).getProjects
  }

  /** Backlogの課題タイプ一覧を取得する
    *
    * @param backlogAuthInfoParams Backlogの認証情報
    * @param projectId プロジェクトID
    * @return 課題タイプ一覧
    */
  def getIssueTypes(
      backlogAuthInfoParams: BacklogAuthInfoParams,
      projectId: String
  ): ResponseList[IssueType] = {
    initialize(backlogAuthInfoParams).getIssueTypes(projectId)
  }

  private def initialize(
      backlogAuthInfoEntity: BacklogAuthInfoParams
  ): BacklogClient = {
    val configure: BacklogConfigure = new BacklogComConfigure(
      backlogAuthInfoEntity.spaceId
    ).apiKey(backlogAuthInfoEntity.apiKey)
    new BacklogClientFactory(configure).newClient
  }

}
