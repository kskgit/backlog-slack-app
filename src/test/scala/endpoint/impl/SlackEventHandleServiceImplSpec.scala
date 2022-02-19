package endpoint.impl

import com.slack.api.bolt.context.builtin.MessageShortcutContext
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import com.slack.api.methods.request.chat.ChatPostEphemeralRequest
import com.slack.api.model.block.Blocks.{asBlocks, divider, section}
import com.slack.api.model.block.composition.BlockCompositions.{asOptions, markdownText, option}
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.element.BlockElements.staticSelect
import entity.BacklogAuthInfoEntity
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import repository.impl.{BacklogRepositoryImpl, FireStoreRepositoryImpl}
import service.impl.SlackEventHandleServiceImpl

// TODO: 再実装
class SlackEventHandleServiceImplSpec extends AnyFunSuite with MockitoSugar {
  test("createIssueMessageShortcutHandler 認証情報ありの場合") {
    //
    // 事前準備
    //
    val req = mock[MessageShortcutRequest](Mockito.RETURNS_DEEP_STUBS)
    val ctx = mock[MessageShortcutContext](Mockito.RETURNS_DEEP_STUBS)
    val request = ChatPostEphemeralRequest.builder()
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
    // SlackEventHandleServiceImpl生成
    val backlogRepository = mock[BacklogRepositoryImpl](Mockito.RETURNS_DEEP_STUBS)
    val storeRepository = mock[FireStoreRepositoryImpl](Mockito.RETURNS_DEEP_STUBS)
    when(storeRepository.getBacklogAuthInfo(
      req.getPayload.getChannel.getId, req.getPayload.getUser.getId)) thenReturn(BacklogAuthInfoEntity("spaceId", "apiKey"))
    val slackEventHandleService = SlackEventHandleServiceImpl(backlogRepository, storeRepository)()

    //
    // 処理実施
    //
    slackEventHandleService.acceptCreateIssueRequest(req, ctx)

    //
    // 結果確認
    //
    verify(ctx.client, times(1)).chatPostEphemeral(request.build())
    verify(storeRepository, times(1)).getBacklogAuthInfo(
      req.getPayload.getChannel.getId, req.getPayload.getUser.getId)
    verify(ctx, times(1)).ack
  }

}