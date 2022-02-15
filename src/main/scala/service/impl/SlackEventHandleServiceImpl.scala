package service.impl

import com.slack.api.bolt.handler.builtin.MessageShortcutHandler
import repository.BacklogRepository
import service.SlackEventHandleService

import javax.inject.Inject

case class SlackEventHandleServiceImpl @Inject()
    (backlogRepository: BacklogRepository)() extends SlackEventHandleService {

  override def createIssueMessageShortcutHandler: MessageShortcutHandler = (req, ctx) => {
    // TODO: DBからユーザーのプロジェクトを取得
    //  取得出来ればそれを設定したブロックを返す
    //  取得不可であれば設定用のブロックを返す
    // TODO: 課題タイプを選択してもらう
    //  もしくは固定にする
    backlogRepository.createIssue(req)
    ctx.ack()
  }
}