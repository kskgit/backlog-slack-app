package service.impl

import com.slack.api.app_backend.views.response.ViewSubmissionResponse
import com.slack.api.bolt.handler.builtin.{MessageShortcutHandler, ViewSubmissionHandler}
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import com.slack.api.methods.request.chat.ChatGetPermalinkRequest
import com.slack.api.methods.request.views.ViewsOpenRequest.ViewsOpenRequestBuilder
import com.slack.api.model.block.Blocks.{asBlocks, input, section}
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.{OptionObject, PlainTextObject}
import com.slack.api.model.block.element.BlockElements.{plainTextInput, staticSelect}
import com.slack.api.model.view.Views.{view, viewClose, viewSubmit, viewTitle}
import com.slack.api.model.view.{View, ViewSubmit}
import entity.BacklogAuthInfoEntity
import repository.{BacklogRepository, StoreRepository}
import service.SlackEventHandleService

import java.util
import javax.inject.Inject

// TODO: requestはそれぞれのイベントに依存するため、そこから値を取り出す処理はここで実装に統一
// TODO: 最初だけ"We had some trouble connecting. Try again?" が表示される
case class SlackEventHandleServiceImpl @Inject()
    (backlogRepository: BacklogRepository, storeRepository:StoreRepository)() extends SlackEventHandleService {

  override def acceptCreateIssueRequest: MessageShortcutHandler = (req, ctx) => {
    // TODO: FireStoreから認証情報を取得
    // 会話のURLをStoreに保存
    val permalink = ctx.client().chatGetPermalink(
      ChatGetPermalinkRequest.builder().channel(req.getPayload.getChannel.getId).messageTs(req.getPayload.getMessageTs).build()
    ).getPermalink

    storeRepository.createMostRecentMessageLink(req.getPayload.getTeam.getId, req.getPayload.getUser.getId, permalink)
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getTeam.getId, req.getPayload.getUser.getId)
    if (backlogAuthInfo.apiKey == "" || backlogAuthInfo.spaceId == "") {
      ctx.client().viewsOpen((r:ViewsOpenRequestBuilder) => getInputAuthInfoViewBuilder(r, req))
    } else {
      ctx.client().viewsOpen((r:ViewsOpenRequestBuilder) => {
        getInputIssueInfoViewBuilder(r, req, getProjectOptions(backlogAuthInfo))
      })
    }
    storeRepository.createMostRecentMessageLink(req.getPayload.getTeam.getId, req.getPayload.getUser.getId, permalink)
    ctx.ack()
    // TODO: 課題登録が失敗した際のエラーメッセージをチャットへ通知する
  }


  override def registrationAuthInfoToStore: ViewSubmissionHandler = (req, ctx) => {
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getTeam.getId, req.getPayload.getUser.getId)
    // TODO: 登録する前に以下で認証情報の確認
    //  https://developer.nulab.com/ja/docs/backlog/api/2/get-own-user/#
    // TODO: FireStoreへ登録
    def getUser = req.getPayload.getUser
    storeRepository.createBacklogAuthInfo(getUser.getTeamId, getUser.getId, req)
    val response = ViewSubmissionResponse.builder()
      .responseAction("update")
      .view(getInputIssueInfoView(getProjectOptions(backlogAuthInfo)))
      .build()
    ctx.ack(response)
  }

  override def registrationIssueToBacklog: ViewSubmissionHandler = (req, ctx) => {
    val getViewValues = req.getPayload.getView.getState.getValues
    val projectId = getViewValues.get("pjId").get("acId").getSelectedOption.getValue
    val issueTitle = getViewValues.get("ipId").get("acId").getValue
    // TODO: 固定値設定を修正する
    val issueTypeId = 1273155
    val messageLink = storeRepository.getMostRecentMessageLink(req.getPayload.getTeam.getId, req.getPayload.getUser.getId)
    val createIssueParams = backlogRepository.getCreateIssueParams(projectId, issueTitle, issueTypeId, messageLink)
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getTeam.getId, req.getPayload.getUser.getId)
    // TODO: 認証情報が無い場合のエラー処理
    // TODO: 認証情報をFireStoreからの取得へ変更
    val url = backlogRepository.createIssue(createIssueParams,backlogAuthInfo)
    // TODO: 登録失敗した場合のエラー処理
    val response = ViewSubmissionResponse.builder()
      .responseAction("update")
      .view(getCreatedIssueInfoView(url))
      .build();
    ctx.ack(response)
  }

  // TODO: 課題タイプをどうするか確認する
  private def getInputIssueInfoViewBuilder(r:ViewsOpenRequestBuilder, req: MessageShortcutRequest
                                           , options: util.List[OptionObject]): ViewsOpenRequestBuilder = {
    r.triggerId(req.getPayload.getTriggerId)
      .view(getInputIssueInfoView(options))
  }

  private def getInputIssueInfoView(options: util.List[OptionObject]): View = {
    View
      .builder()
      .`type`("modal")
      .callbackId("registration-issue-to-backlog")
      .title(viewTitle(vt => vt.`type`("plain_text").text("課題を登録する")))
      .close(viewClose(c=>c.`type`("plain_text").text("閉じる")))
      .submit(viewSubmit((submit: ViewSubmit.ViewSubmitBuilder) => submit.`type`("plain_text").text("送信").emoji(true)))
      .blocks(
        asBlocks(
          input(i => i.element(staticSelect(
            ss =>
              ss.actionId("acId").options(
                options
              ).placeholder(PlainTextObject.builder().text("プロジェクトを選択してください").build())
          )).label(PlainTextObject.builder().text("プロジェクト").build()).blockId("pjId"))
          ,input(
            i => i.element(plainTextInput(
              pt =>
                pt.actionId("acId").placeholder(
                  PlainTextObject.builder().text("タイトルを入力してください").build())
            )).label(PlainTextObject.builder().text("タイトル").build()).blockId("ipId"))
        )
      ).build()
  }

  private def getInputAuthInfoViewBuilder(r:ViewsOpenRequestBuilder, req: MessageShortcutRequest): ViewsOpenRequestBuilder = {
    r.triggerId(req.getPayload.getTriggerId)
      .view(view(v=>
        v
          .`type`("modal")
          .callbackId("registration-auth-info-to-store")
          .title(viewTitle(vt => vt.`type`("plain_text").text("認証情報を入力する")))
          .close(viewClose(c=>c.`type`("plain_text").text("閉じる")))
          .submit(viewSubmit((submit: ViewSubmit.ViewSubmitBuilder) => submit.`type`("plain_text").text("送信").emoji(true)))
          .blocks(
            asBlocks(
              input(
                i => i.element(plainTextInput(
                  pt =>
                    pt.actionId("apiAction").placeholder(
                      PlainTextObject.builder().text("APIキーを入力してください").build())
                )).label(PlainTextObject.builder().text("APIキー").build()).blockId("apiBlock"))
              ,input(
                i => i.element(plainTextInput(
                  pt =>
                    pt.actionId("spaceAction").placeholder(
                      PlainTextObject.builder().text("スペースIDを入力してください").build())
                )).label(PlainTextObject.builder().text("スペースID").build()).blockId("spaceBlock"))
            )
          )
      ))
  }

  private def getCreatedIssueInfoView(url:String) :View = {
    View
      .builder()
      .`type`("modal")
      .title(viewTitle(vt => vt.`type`("plain_text").text("課題の登録に成功しました")))
      .close(viewClose(c=>c.`type`("plain_text").text("閉じる")))
      .blocks(
        asBlocks(
          section(s=>s.text(markdownText(s"$url")))
        )
      ).build()
  }

  private def getProjectOptions(authInfoEntity:BacklogAuthInfoEntity): util.ArrayList[OptionObject] = {
    val projects = backlogRepository.getProjects(authInfoEntity)
    // BuildKitに渡すために、JavaのArrayListを使用する必要あり
    val options = new java.util.ArrayList[OptionObject]()
    projects.forEach(p=> options.add(OptionObject.builder()
      .value(p.getId.toString)
      .text(PlainTextObject.builder().text(p.getName).build())
      .build())
    )
    options
  }
 // TODO: 認証情報が無い場合のユーザへのエラーメッセージ
}