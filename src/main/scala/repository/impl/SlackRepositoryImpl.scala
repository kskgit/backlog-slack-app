package repository.impl

import com.slack.api.bolt.handler.builtin.MessageShortcutHandler
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import com.slack.api.methods.request.chat.ChatPostEphemeralRequest
import com.slack.api.model.block.Blocks.{asBlocks, divider, section}
import com.slack.api.model.block.composition.BlockCompositions.{asOptions, markdownText, option}
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.element.BlockElements.staticSelect
import repository.SlackRepository

class SlackRepositoryImpl extends SlackRepository {

  override def postInputIssueInfoRequest: MessageShortcutHandler = (req, ctx) => {
    ctx.client().chatPostEphemeral(createEphemeralRequest(req))
    ctx.ack()
  }

  private def createEphemeralRequest(req: MessageShortcutRequest): ChatPostEphemeralRequest = {
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
  }

  override def postBacklogAuthInfoRequest: MessageShortcutHandler = (req, ctx) => {
    // TODO: 実装
    ctx.ack()
  }

}
