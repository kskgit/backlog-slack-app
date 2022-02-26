package repository

import params.BacklogAuthInfoParams

// NOTE: FireStore意外のStoreを使用する可能性があるため、StoreRepositoryという名前で作成
//  各メソッド用のパラメータクラス作成を検討するも、抽象クラス（StoreRepository）用のパラメータクラスと
//  実装クラス（FireStoreRepositoryImpl）用のパラメータクラスのパラメータ値を一致させる必要があり
//  抽象化できなかったため未作成
trait StoreRepository {

  /** BacklogAPIの使用に必要な認証情報を取得する
    * @param teamId SlackのTeamId
    * @param userId SlackのUserId
    * @return BacklogAPIの使用に必要な認証情報
    */
  def getBacklogAuthInfo(
      teamId: String,
      userId: String
  ): BacklogAuthInfoParams

  /** BacklogAPIの使用に必要な認証情報を保存する
    * @param teamId SlackのTeamId
    * @param userId SlackのUserId
    * @param apiKey BacklogのAPIキー
    * @param spaceId BacklogのスペースID
    */
  def createBacklogAuthInfo(
      teamId: String,
      userId: String,
      apiKey: String,
      spaceId: String
  ): Unit

  /** Slackの会話URLを保存する
    * @param teamId SlackのTeamId
    * @param userId SlackのUserId
    * @return Slackの会話URL
    */
  def getMostRecentMessageLink(teamId: String, userId: String): String
  def createMostRecentMessageLink(
      teamId: String,
      userId: String,
      url: String
  ): Unit
}
