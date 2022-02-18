package service.impl

import com.slack.api.bolt.handler.builtin.MessageShortcutHandler
import com.slack.api.methods.request.chat.{ChatPostEphemeralRequest, ChatPostMessageRequest}
import com.slack.api.model.block.Blocks.{actions, asBlocks, divider, section}
import com.slack.api.model.block.composition.BlockCompositions.{asOptions, asSectionFields, markdownText, option}
import com.slack.api.model.block.composition.{BlockCompositions, OptionObject, PlainTextObject}
import com.slack.api.model.block.element.BlockElements.{asElements, button, multiStaticSelect, staticSelect, usersSelect}
import repository.BacklogRepository
import service.SlackEventHandleService

import javax.inject.Inject

case class SlackEventHandleServiceImpl @Inject()
    (backlogRepository: BacklogRepository)() extends SlackEventHandleService {

  override def createIssueMessageShortcutHandler: MessageShortcutHandler = (req, ctx) => {
    val request = ChatPostEphemeralRequest.builder()
      .channel(req.getPayload.getChannel.getId)
      .user(req.getPayload.getUser.getId)
      .blocks(
        asBlocks(
          section(s => s.text(markdownText("*課題についていくつか選択してください:*"))),
          divider(),
          section(s => s.text(markdownText("*Static Select:*")).accessory(
            staticSelect(
              ss =>
                ss.actionId("block-multi-static-select-action") // TODO: 変更する
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
    // TODO: 失敗した際のエラーメッセージをチャットへ通知する
    ctx.client().chatPostEphemeral(request)
    ctx.ack()
  }
}