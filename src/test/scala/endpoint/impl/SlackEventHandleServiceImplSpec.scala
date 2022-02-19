package endpoint.impl

import com.slack.api.bolt.context.builtin.MessageShortcutContext
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import com.slack.api.methods.request.chat.ChatPostEphemeralRequest
import com.slack.api.model.block.Blocks.{asBlocks, divider, section}
import com.slack.api.model.block.composition.BlockCompositions.{asOptions, markdownText, option}
import com.slack.api.model.block.composition.PlainTextObject
import com.slack.api.model.block.element.BlockElements.staticSelect
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import repository.impl.BacklogRepositoryImpl
import service.impl.SlackEventHandleServiceImpl

// TODO: 再実装
class SlackEventHandleServiceImplSpec extends AnyFunSuite with MockitoSugar {
//  test("createIssueMessageShortcutHandler テスト") {
//    //
//    // 事前準備
//    //
//    val messageShortCutRequest = mock[MessageShortcutRequest](Mockito.RETURNS_DEEP_STUBS)
//    val messageShortCutContext = mock[MessageShortcutContext](Mockito.RETURNS_DEEP_STUBS)
//    val request = ChatPostEphemeralRequest.builder()
//      .blocks(
//        asBlocks(
//          section(s => s.text(markdownText("*課題についていくつか選択してください:*"))),
//          divider(),
//          section(s => s.text(markdownText("*Static Select:*")).accessory(
//            staticSelect(
//              ss =>
//                ss.actionId("block-multi-static-select-action") // TODO: 変更する
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
//    // SlackEventHandleServiceImpl生成
//    val backlogRepository = mock[BacklogRepositoryImpl] // TODO: 未使用のため削除を検討する
//    val slackEventHandleService = SlackEventHandleServiceImpl(backlogRepository)()
//
//    //
//    // 処理実施
//    //
//    slackEventHandleService.createIssueMessageShortcutHandler(messageShortCutRequest, messageShortCutContext)
//
//    //
//    // 結果確認
//    //
//    verify(messageShortCutContext, times(1)).ack
//    verify(messageShortCutContext.client, times(1)).chatPostEphemeral(request.build())
//    verify(messageShortCutRequest.getPayload.getChannel, times(1)).getId
//    verify(messageShortCutRequest.getPayload.getUser, times(1)).getId
//  }
}
