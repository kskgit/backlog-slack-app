package service.impl

import com.slack.api.app_backend.views.response.ViewSubmissionResponse
import com.slack.api.bolt.handler.builtin.{BlockActionHandler, MessageShortcutHandler, ViewSubmissionHandler}
import com.slack.api.bolt.request.builtin.{BlockActionRequest, MessageShortcutRequest}
import com.slack.api.methods.request.chat.ChatGetPermalinkRequest
import com.slack.api.methods.request.views.ViewsOpenRequest.ViewsOpenRequestBuilder
import com.slack.api.methods.request.views.ViewsUpdateRequest.ViewsUpdateRequestBuilder
import com.slack.api.model.block.Blocks.{asBlocks, input, section}
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.{OptionObject, PlainTextObject}
import com.slack.api.model.block.element.BlockElements.{plainTextInput, staticSelect}
import com.slack.api.model.view.Views.{view, viewClose, viewSubmit, viewTitle}
import com.slack.api.model.view.{View, ViewSubmit}
import constant.SlackEventType
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
        getSelectProjectViewBuilder(r, req, getProjectOptions(backlogAuthInfo))
      })
    }
    storeRepository.createMostRecentMessageLink(req.getPayload.getTeam.getId, req.getPayload.getUser.getId, permalink)
    ctx.ack()
    // TODO: 課題登録が失敗した際のエラーメッセージをチャットへ通知する
  }
  override def postIssueInfoReqToSlack: BlockActionHandler = (req, ctx) => {
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getTeam.getId, req.getPayload.getUser.getId)
    val projectId = req.getPayload.getView.getState.getValues.get("pjBk").get("post-issue-info-req-to-slack").getSelectedOption.getValue
    ctx.client().viewsUpdate((r:ViewsUpdateRequestBuilder) => {
      // TODO: 課題種別一覧を仕込む
      getInputIssueInfoViewBuilder(r,req,getIssueTypes(backlogAuthInfo, projectId))
    })
    ctx.ack()
  }

  override def registrationAuthInfoToStore: ViewSubmissionHandler = (req, ctx) => {
    // TODO: 登録する前に以下で認証情報の確認
    //  https://developer.nulab.com/ja/docs/backlog/api/2/get-own-user/#
    def getUser = req.getPayload.getUser
    storeRepository.createBacklogAuthInfo(getUser.getTeamId, getUser.getId, req)
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getTeam.getId, req.getPayload.getUser.getId)
    val response = ViewSubmissionResponse.builder()
      .responseAction("update")
      .view(getSelectProjectView(getProjectOptions(backlogAuthInfo)))
      .build()
    ctx.ack(response)
  }

  override def registrationIssueToBacklog: ViewSubmissionHandler = (req, ctx) => {
    val getViewValues = req.getPayload.getView.getState.getValues
    val projectId = getViewValues.get("pjId").get("acId").getSelectedOption.getValue
    val issueTitle = getViewValues.get("ipId").get("acId").getValue

    val map = projectId.split(",").map(_.split(":"))
      .map { case Array(k, v) => (k, v)}.toMap

    val messageLink = storeRepository.getMostRecentMessageLink(req.getPayload.getTeam.getId, req.getPayload.getUser.getId)

    val createIssueParams = backlogRepository.getCreateIssueParams(map("projectId"), issueTitle, map("issueTypeId").toInt, messageLink)
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
  private def getInputIssueInfoViewBuilder(r:ViewsUpdateRequestBuilder, req: BlockActionRequest
                                           , options: util.List[OptionObject]): ViewsUpdateRequestBuilder = {
    r.view(getInputIssueInfoView(options)).viewId(req.getPayload.getView.getId)
  }

  private def getInputIssueInfoView(options: util.List[OptionObject]): View = {
    View
      .builder()
      .`type`("modal")
      .callbackId(SlackEventType.RegistrationIssueToBacklog.typeName)
      .title(viewTitle(vt => vt.`type`("plain_text").text("課題を登録する")))
      .close(viewClose(c=>c.`type`("plain_text").text("閉じる")))
      .submit(viewSubmit((submit: ViewSubmit.ViewSubmitBuilder) => submit.`type`("plain_text").text("送信").emoji(true)))
      .blocks(
        asBlocks(
          // TODO: これが課題種別にすり替わる
          input(i => i.element(staticSelect(
            ss =>
              ss.actionId("acId").options(
                options
              ).placeholder(PlainTextObject.builder().text("課題種別を選択してください").build())
          )).label(PlainTextObject.builder().text("課題種別").build()).blockId("pjId"))
          ,input(
            i => i.element(plainTextInput(
              pt =>
                pt.actionId("acId").placeholder(
                  PlainTextObject.builder().text("タイトルを入力してください").build())
            )).label(PlainTextObject.builder().text("タイトル").build()).blockId("ipId"))
        )
      ).build()
  }
  private def getSelectProjectViewBuilder(r:ViewsOpenRequestBuilder, req: MessageShortcutRequest
                                           , options: util.List[OptionObject]): ViewsOpenRequestBuilder = {
    r.triggerId(req.getPayload.getTriggerId)
      .view(getSelectProjectView(options))
  }
  private def getSelectProjectView(options: util.List[OptionObject]): View = {
    View
      .builder()
      .`type`("modal")
      .callbackId(SlackEventType.PostIssueInfoReqToSlack.typeName)
      .title(viewTitle(vt => vt.`type`("plain_text").text("プロジェクトを選択")))
      .close(viewClose(c=>c.`type`("plain_text").text("閉じる")))
      .blocks(
        asBlocks(
          section(s=>s.accessory(
            staticSelect(s=>s.actionId("post-issue-info-req-to-slack").options(options))
          ).text(PlainTextObject.builder().text("プロジェクトを選択してください").build()).blockId("pjBk"))
        )
      ).build()
  }

  private def getInputAuthInfoViewBuilder(r:ViewsOpenRequestBuilder, req: MessageShortcutRequest): ViewsOpenRequestBuilder = {
    r.triggerId(req.getPayload.getTriggerId)
      .view(view(v=>
        v
          .`type`("modal")
          .callbackId(SlackEventType.RegistrationAuthInfoToStore.typeName)
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

  private def getIssueTypes(authInfoEntity:BacklogAuthInfoEntity, projectId:String): util.ArrayList[OptionObject] = {
    val projects = backlogRepository.getIssueTypes(authInfoEntity,projectId)
    // BuildKitに渡すために、JavaのArrayListを使用する必要あり
    val options = new java.util.ArrayList[OptionObject]()
    projects.forEach(p=>
      options.add(OptionObject.builder()
      .value("projectId:" + p.getProjectId.toString + "," + "issueTypeId:" + p.getId.toString)
      .text(PlainTextObject.builder().text(p.getName).build())
      .build())
    )
    options
  }
 // TODO: 認証情報が無い場合のユーザへのエラーメッセージ
}