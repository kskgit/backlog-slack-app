package service.impl

import com.slack.api.bolt.handler.builtin.{BlockActionHandler, MessageShortcutHandler}
import repository.{BacklogRepository, SlackRepository, StoreRepository}
import service.SlackEventHandleService

import javax.inject.Inject

case class SlackEventHandleServiceImpl @Inject()
    (backlogRepository: BacklogRepository, slackRepository:SlackRepository, storeRepository:StoreRepository)() extends SlackEventHandleService {

  override def createIssueMessageShortcutHandler: MessageShortcutHandler = (req, ctx) => {
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getChannel.getId, req.getPayload.getUser.getId)
    if (backlogAuthInfo == null) {
      slackRepository.postBacklogAuthInfoRequest(req,ctx)
    } else {
      slackRepository.postInputIssueInfoRequest(req,ctx)
    }
    // TODO: 課題登録が失敗した際のエラーメッセージをチャットへ通知する
  }

  override def submitIssueBlockActionHandler: BlockActionHandler = (req, ctx) => {
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(req.getPayload.getChannel.getId, req.getPayload.getUser.getId)
    // TODO: 認証情報が無い場合のエラー処理
    slackRepository.registrationIssueToBacklog(req,ctx)
  }
}