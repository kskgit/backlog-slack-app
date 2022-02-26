package service

import com.slack.api.bolt.handler.builtin.{
  BlockActionHandler,
  MessageShortcutHandler,
  ViewSubmissionHandler
}

trait SlackEventHandleService {

  /** ユーザが Slack画面上で「Backlogに課題として登録する」を押下することで発火するイベントを処理するEventHandler
    *
    * @return 認証情報がある場合はプロジェクトを選択するView ない場合は認証情報を入力するView
    */
  def acceptCreateIssueRequest: MessageShortcutHandler

  /** ユーザが このアプリから返されたView上でプロジェクトを選択することで発火するEventHandler
    *
    * @return 課題情報を入力するView
    */
  def postIssueInfoReqToSlack: BlockActionHandler

  /** ユーザが このアプリから返されたView上で認証情報を入力することで発火するEventHandler
    *
    * @return プロジェクトを選択するView
    */
  def registrationAuthInfoToStore: ViewSubmissionHandler

  /** ユーザが このアプリから返されたView上で課題情報を入力することで発火するEventHandler
    *
    * @return 課題情報を入力するView
    */
  def registrationIssueToBacklog: ViewSubmissionHandler
}
