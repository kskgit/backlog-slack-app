package repository.client

import com.nulabinc.backlog4j.api.option.CreateIssueParams
import com.nulabinc.backlog4j.{
  BacklogClient,
  BacklogClientFactory,
  Project,
  ResponseList
}
import com.nulabinc.backlog4j.conf.{BacklogComConfigure, BacklogConfigure}
import params.BacklogAuthInfoParams

// TODO: クライアントのメソッドをラップしたメソッド作成
case class BacklogClientImpl() {
  // TODO: 最後にprivateへ変更する
  def initialize(
      backlogAuthInfoEntity: BacklogAuthInfoParams
  ): BacklogClient = {
    val configure: BacklogConfigure = new BacklogComConfigure(
      backlogAuthInfoEntity.spaceId
    ).apiKey(backlogAuthInfoEntity.apiKey)
    new BacklogClientFactory(configure).newClient
  }

  def createIssue(
      createIssueParams: CreateIssueParams,
      backlogAuthInfoParams: BacklogAuthInfoParams
  ): String = {
    val backlogClientAuthed = initialize(backlogAuthInfoParams)
    val issue = backlogClientAuthed.createIssue(createIssueParams)
    backlogClientAuthed.getIssueUrl(issue)
  }

  def getProjects(
      backlogAuthInfoParams: BacklogAuthInfoParams
  ): ResponseList[Project] = {
    initialize(backlogAuthInfoParams).getProjects
  }

}
