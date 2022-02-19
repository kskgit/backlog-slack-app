package service.impl

import com.slack.api.bolt.handler.builtin.{BlockActionHandler, MessageShortcutHandler}
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import com.slack.api.methods.request.chat.ChatPostEphemeralRequest
import com.slack.api.model.block.Blocks.{asBlocks, divider, section}
import com.slack.api.model.block.composition.BlockCompositions.{asOptions, markdownText, option}
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.element.BlockElements.staticSelect
import repository.{BacklogRepository, StoreRepository}
import service.SlackEventHandleService

import javax.inject.Inject

case class SlackEventHandleServiceImpl @Inject()
    (backlogRepository: BacklogRepository, storeRepository:StoreRepository)() extends SlackEventHandleService {

  override def acceptCreateIssueRequest: MessageShortcutHandler = (req, ctx) => {
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getChannel.getId, req.getPayload.getUser.getId)
    if (backlogAuthInfo == null) {
    } else {
      // TODO: Backlogから課題の一覧を取得して渡す
      ctx.client().chatPostEphemeral(createInputIssueInfoRequest(req))
    }
    ctx.ack()
    // TODO: 課題登録が失敗した際のエラーメッセージをチャットへ通知する
  }

  // TODO: Backlogから課題の一覧を引数で受け取る
  private def createInputIssueInfoRequest(req: MessageShortcutRequest): ChatPostEphemeralRequest = {
    ChatPostEphemeralRequest.builder()
      .channel(req.getPayload.getChannel.getId)
      .user(req.getPayload.getUser.getId)
      .blocks(
        asBlocks(
          section(s => s.text(markdownText("*課題についていくつか選択してください:*"))),
          divider(),
          section(s => s.text(markdownText("*Static Select:*")).accessory(
            staticSelect(
              ss =>
                ss.actionId("registration-issue-to-backlog") // TODO: 変更する
                  .options(
                    asOptions( // TODO: Backlogから取得したユーザーのプロジェクト一覧を仕込む
                      option(option => option.text(PlainTextObject.builder().text("プロジェクト1").build())),
                      option(option => option.text(PlainTextObject.builder().text("プロジェクト2").build()))
                    )
                  )
            )
          )
          )
        )
      )
      .build()
  }

  override def registrationAuthInfoToStore: BlockActionHandler = (req, ctx) => {
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getChannel.getId, req.getPayload.getUser.getId)
    // TODO: 認証情報が無い場合のエラー処理
    // TODO: Backlogへ登録
//    println("ブロックアクション到達")
    ctx.ack()
  }

  override def registrationIssueToBacklog: BlockActionHandler = (req, ctx) => {
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getChannel.getId, req.getPayload.getUser.getId)
    // TODO: 認証情報が無い場合のエラー処理
    // TODO: Backlogへ登録
    println("ブロックアクション到達")
    ctx.ack()
  }
}