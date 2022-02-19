package service.impl

import com.slack.api.bolt.handler.builtin.{BlockActionHandler, MessageShortcutHandler, ViewSubmissionHandler}
import com.slack.api.methods.request.views.ViewsOpenRequest.ViewsOpenRequestBuilder
import com.slack.api.model.block.Blocks.{asBlocks, input, section}
import com.slack.api.model.block.composition.BlockCompositions.{asOptions, markdownText, option}
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.element.BlockElement
import com.slack.api.model.block.element.BlockElements.{asElements, plainTextInput, staticSelect}
import com.slack.api.model.view.ViewSubmit
import com.slack.api.model.view.Views.{view, viewClose, viewSubmit, viewTitle}
import entity.BacklogAuthInfoEntity
import repository.{BacklogRepository, StoreRepository}
import service.SlackEventHandleService

import javax.inject.Inject

// TODO: 最初だけ"We had some trouble connecting. Try again?" が表示される
case class SlackEventHandleServiceImpl @Inject()
    (backlogRepository: BacklogRepository, storeRepository:StoreRepository)() extends SlackEventHandleService {

  override def acceptCreateIssueRequest: MessageShortcutHandler = (req, ctx) => {
    println("開始")
    // TODO: モーダル生成箇所を分ける
    val res = ctx.client.viewsOpen((r:ViewsOpenRequestBuilder) =>
      r.triggerId(req.getPayload.getTriggerId)
        .view(view(v=>
          v
            .`type`("modal")
            .callbackId("registration-issue-to-backlog") // TODO: callbackIdについて調べる
            .title(viewTitle(vt => vt.`type`("plain_text").text("課題を登録する")))
            .close(viewClose(vc => vc.`type`("plain_text").text("閉じる")))
            .submit(viewSubmit((submit: ViewSubmit.ViewSubmitBuilder) => submit.`type`("plain_text").text("送信").emoji(true)))
            .blocks(
              asBlocks(
                input(i => i.element(staticSelect(
                  ss =>
                    ss.actionId("acId").options(
                      asOptions( // TODO: Backlogから取得したユーザーのプロジェクト一覧を仕込む
                        option(option => option.value("260625").text(PlainTextObject.builder().text("プロジェクト1").build())),
                        option(option => option.value("260626").text(PlainTextObject.builder().text("プロジェクト2").build()))
                      )).placeholder(PlainTextObject.builder().text("プロジェクトを選択してください").build())
                )).label(PlainTextObject.builder().text("プロジェクト").build()).blockId("pjId"))
                ,input(
                  i => i.element(plainTextInput(
                    pt =>
                      pt.actionId("acId").placeholder(
                        PlainTextObject.builder().text("タイトルを入力してください").build())
                  )).label(PlainTextObject.builder().text("タイトル").build()).blockId("ipId"))
              )
            )
          )
        )
    )
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getChannel.getId, req.getPayload.getUser.getId)
    if (backlogAuthInfo == null) {
      val res = ctx.client.viewsOpen((r:ViewsOpenRequestBuilder) => r.view(view(v=>v.blocks(
        asBlocks(
          section(s => s.text(markdownText("*課題についていくつか選択してください:*")))
        ))))
      )
//      println(res)
    } else {
      // TODO: Backlogから課題の一覧を取得して渡す
//      ctx.client().chatPostEphemeral(createInputAuthInfoBlock(req))
    }
    ctx.ack()
    // TODO: 課題登録が失敗した際のエラーメッセージをチャットへ通知する
  }

  // TODO: Backlogから課題の一覧を引数で受け取る

//  private def createInputIssueInfoBlock(req: MessageShortcutRequest): ChatPostEphemeralRequest = {
//    ChatPostEphemeralRequest.builder()
//      .channel(req.getPayload.getChannel.getId)
//      .user(req.getPayload.getUser.getId)
//      .blocks(
//        asBlocks(
//          section(s => s.text(markdownText("*課題についていくつか選択してください:*"))),
//          divider(),
//          section(s => s.text(markdownText("*Static Select:*")).accessory(
//            staticSelect(
//              ss =>
//                ss.actionId("registration-issue-to-backlog") // TODO: 変更する
//                  .options(
//                    asOptions( // TODO: Backlogから取得したユーザーのプロジェクト一覧を仕込む
//                      option(option => option.text(PlainTextObject.builder().text("プロジェクト1").build())),
//                      option(option => option.text(PlainTextObject.builder().text("プロジェクト2").build()))
//                    )
//                  )
//            )
//          )
//          )
//        )
//      )
//      .build()
//  }
//
//  private def createInputAuthInfoBlock(req: MessageShortcutRequest): ChatPostEphemeralRequest = {
//    ChatPostEphemeralRequest.builder()
//      .channel(req.getPayload.getChannel.getId)
//      .user(req.getPayload.getUser.getId)
//      .blocks(
//        asBlocks(
//          section(s => s.text(markdownText("*Backlogの認証情報を入力してください:*"))),
//          divider(),
//          input(input => input
//            .blockId("agenda-block")
//            .element(plainTextInput(pti => pti.actionId("agenda-action").multiline(true)))
////            .label(label => label)
//          )
//        )
//      )
//      .build()
//  }

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
    // TODO: Backlogへ登録

    val authInfoEntity = BacklogAuthInfoEntity(sys.env("BACKLOG_SPACE_ID"), sys.env("BACKLOG_API_KEY"))
    backlogRepository.createIssue(req,authInfoEntity)
    ctx.ack()
  }
  // TODO: 認証情報が無い場合のユーザへのエラーメッセージ
}