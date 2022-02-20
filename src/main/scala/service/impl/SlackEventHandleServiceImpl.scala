package service.impl

import com.slack.api.bolt.handler.builtin.{BlockActionHandler, MessageShortcutHandler, ViewSubmissionHandler}
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import com.slack.api.methods.request.views.ViewsOpenRequest.ViewsOpenRequestBuilder
import com.slack.api.model.block.Blocks.{asBlocks, input}
import com.slack.api.model.block.composition.{OptionObject, PlainTextObject}
import com.slack.api.model.block.element.BlockElements.{plainTextInput, staticSelect}
import com.slack.api.model.view.ViewSubmit
import com.slack.api.model.view.Views.{view, viewClose, viewSubmit, viewTitle}
import entity.BacklogAuthInfoEntity
import repository.{BacklogRepository, StoreRepository}
import service.SlackEventHandleService

import java.util
import javax.inject.Inject


// TODO: 最初だけ"We had some trouble connecting. Try again?" が表示される
case class SlackEventHandleServiceImpl @Inject()
    (backlogRepository: BacklogRepository, storeRepository:StoreRepository)() extends SlackEventHandleService {

  override def acceptCreateIssueRequest: MessageShortcutHandler = (req, ctx) => {
    // TODO: FireStoreから認証情報を取得
    val authInfoEntity = BacklogAuthInfoEntity(sys.env("BACKLOG_SPACE_ID"), sys.env("BACKLOG_API_KEY"))
//    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getChannel.getId, req.getPayload.getUser.getId)
    val backlogAuthInfo = "authInfo"

    if (backlogAuthInfo == null) {
      // TODO: 認証情報を入力するViewを返す
    } else {
      val projects = backlogRepository.getProjects(authInfoEntity)
      val options = new java.util.ArrayList[OptionObject]()
      projects.forEach(p=> options.add(OptionObject.builder()
        .value(p.getId.toString)
        .text(PlainTextObject.builder().text(p.getName).build())
        .build())
      )
      ctx.client().viewsOpen((r:ViewsOpenRequestBuilder) => createInputIssueInfoView(r, req, options))
    }
    ctx.ack()
    // TODO: 課題登録が失敗した際のエラーメッセージをチャットへ通知する
  }

  // TODO: 課題タイプをどうするか確認する
  private def createInputIssueInfoView(r:ViewsOpenRequestBuilder, req: MessageShortcutRequest, options: util.List[OptionObject]): ViewsOpenRequestBuilder = {
    r.triggerId(req.getPayload.getTriggerId)
      .view(view(v=>
        v
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
          )
      ))
  }


  override def registrationAuthInfoToStore: BlockActionHandler = (req, ctx) => {
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getChannel.getId, req.getPayload.getUser.getId)
    // TODO: 登録する前に以下で認証情報の確認
    //  https://developer.nulab.com/ja/docs/backlog/api/2/get-own-user/#
    // TODO: FireStoreへ登録
    ctx.ack()
  }

  override def registrationIssueToBacklog: ViewSubmissionHandler = (req, ctx) => {
//    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getResponseUrls, req.getPayload.getUser.getId)
    // TODO: 認証情報が無い場合のエラー処理
    // TODO: 認証情報をFireStoreからの取得へ変更
    val authInfoEntity = BacklogAuthInfoEntity(sys.env("BACKLOG_SPACE_ID"), sys.env("BACKLOG_API_KEY"))
    backlogRepository.createIssue(req,authInfoEntity)
    ctx.ack()
  }
 // TODO: 認証情報が無い場合のユーザへのエラーメッセージ
}