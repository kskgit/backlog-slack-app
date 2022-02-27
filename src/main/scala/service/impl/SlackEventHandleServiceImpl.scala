package service.impl

import com.slack.api.app_backend.views.response.ViewSubmissionResponse
import com.slack.api.bolt.context.builtin.{
  MessageShortcutContext,
  ViewSubmissionContext
}
import com.slack.api.bolt.handler.builtin.{
  BlockActionHandler,
  MessageShortcutHandler,
  ViewSubmissionHandler
}
import com.slack.api.bolt.request.builtin.{
  BlockActionRequest,
  MessageShortcutRequest,
  ViewSubmissionRequest
}
import com.slack.api.methods.request.chat.ChatGetPermalinkRequest
import com.slack.api.methods.request.views.ViewsOpenRequest.ViewsOpenRequestBuilder
import com.slack.api.methods.request.views.ViewsUpdateRequest.ViewsUpdateRequestBuilder
import com.slack.api.model.block.Blocks.{asBlocks, input, section}
import com.slack.api.model.block.composition.BlockCompositions.markdownText
import com.slack.api.model.block.composition.{OptionObject, PlainTextObject}
import com.slack.api.model.block.element.BlockElements.{
  plainTextInput,
  staticSelect
}
import com.slack.api.model.view.Views.{view, viewClose, viewSubmit, viewTitle}
import com.slack.api.model.view.{View, ViewSubmit}
import constant.SlackEventTypes
import params.BacklogAuthInfoParams
import repository.{BacklogRepository, StoreRepository}
import service.SlackEventHandleService

import java.util
import javax.inject.Inject

case class SlackEventHandleServiceImpl @Inject() (
    backlogRepository: BacklogRepository,
    storeRepository: StoreRepository
)() extends SlackEventHandleService {

  // Viewの要素へ指定するID
  private final val SELECT_PROJECT_BLOCK = "selectProjectBlock"
  private final val INPUT_API_KEY_BLOCK = "inputApiKeyBlock"
  private final val INPUT_API_KEY_ACTION = "inputApiKeyAction"
  private final val INPUT_SPACE_ID_BLOCK = "inputSpaceIdBlock"
  private final val INPUT_SPACE_ID_ACTION = "inputSpaceIdAction"
  private final val SELECT_ISSUE_TYPE_BLOCK = "selectIssueTypeBlock"
  private final val SELECT_ISSUE_TYPE_ACTION = "selectIssueTypeAction"
  private final val SELECT_ISSUE_TITLE_BLOCK = "selectIssueTitleBlock"
  private final val SELECT_ISSUE_TITLE_ACTION = "selectIssueTitleAction"
  private final val PROJECT_ID = "projectID"
  private final val ISSUE_TYPE_ID = "issueTypeId"

  /*
   * === Slackから受け取ったイベントをハンドリングする処理 開始===
   */

  override def acceptCreateIssueRequest: MessageShortcutHandler = (req, ctx) =>
    {
      // 会話のURLをStoreへ保存
      createMostRecentMessageLink(req, ctx)

      // 認証情報を取得
      val backlogAuthInfo = storeRepository.getBacklogAuthInfo(
        req.getPayload.getTeam.getId,
        req.getPayload.getUser.getId
      )

      if (backlogAuthInfo.apiKey == "" || backlogAuthInfo.spaceId == "") {
        // 認証情報入力用のViewを返す
        ctx
          .client()
          .viewsOpen((r: ViewsOpenRequestBuilder) =>
            getInputAuthInfoViewBuilder(r, req)
          )
      } else {
        // プロジェクト選択用のViewを返す
        val projects = getProjectOptions(backlogAuthInfo)
        ctx
          .client()
          .viewsOpen((r: ViewsOpenRequestBuilder) => {
            getSelectProjectViewBuilder(
              r,
              req,
              projects
            )
          })
      }
      ctx.ack()
    }

  override def postIssueInfoReqToSlack: BlockActionHandler = (req, ctx) => {
    val backlogAuthInfo = storeRepository.getBacklogAuthInfo(
      req.getPayload.getTeam.getId,
      req.getPayload.getUser.getId
    )
    val projectId = req.getPayload.getView.getState.getValues
      .get(SELECT_PROJECT_BLOCK)
      .get(SlackEventTypes.PostIssueInfoReqToSlack.typeName)
      .getSelectedOption
      .getValue
    ctx
      .client()
      .viewsUpdate((r: ViewsUpdateRequestBuilder) => {
        getInputIssueInfoViewBuilder(
          r,
          req,
          getIssueTypes(backlogAuthInfo, projectId)
        )
      })
    ctx.ack()
  }

  override def registrationAuthInfoToStore: ViewSubmissionHandler =
    (req, ctx) => {
      // 認証情報を保存
      createRegistrationAuthInfo(req, ctx)
      val backlogAuthInfo = storeRepository.getBacklogAuthInfo(
        req.getPayload.getTeam.getId,
        req.getPayload.getUser.getId
      )
      val response = ViewSubmissionResponse
        .builder()
        .responseAction("update")
        .view(getSelectProjectView(getProjectOptions(backlogAuthInfo)))
        .build()
      ctx.ack(response)
    }

  override def registrationIssueToBacklog: ViewSubmissionHandler = (req, ctx) =>
    {
      val getViewValues = req.getPayload.getView.getState.getValues
      val projectId =
        getViewValues
          .get(SELECT_ISSUE_TYPE_BLOCK)
          .get(SELECT_ISSUE_TYPE_ACTION)
          .getSelectedOption
          .getValue
      val issueTitle = getViewValues
        .get(SELECT_ISSUE_TITLE_BLOCK)
        .get(SELECT_ISSUE_TITLE_ACTION)
        .getValue

      val map = projectId
        .split(",")
        .map(_.split(":"))
        .map { case Array(k, v) => (k, v) }
        .toMap

      val messageLink = storeRepository.getMostRecentMessageLink(
        req.getPayload.getTeam.getId,
        req.getPayload.getUser.getId
      )

      val createIssueParams = backlogRepository.getCreateIssueParams(
        map(PROJECT_ID),
        issueTitle,
        map(ISSUE_TYPE_ID).toInt,
        messageLink
      )
      val backlogAuthInfo = storeRepository.getBacklogAuthInfo(
        req.getPayload.getTeam.getId,
        req.getPayload.getUser.getId
      )

      val url =
        backlogRepository.createIssue(createIssueParams, backlogAuthInfo)

      val response = ViewSubmissionResponse
        .builder()
        .responseAction("update")
        .view(getCreatedIssueInfoView(url))
        .build();
      ctx.ack(response)
    }

  /*
   * === Slackから受け取ったイベントをハンドリングする処理 終了===
   */

  /*
   * === private method 開始===
   */

  private def createMostRecentMessageLink(
      req: MessageShortcutRequest,
      ctx: MessageShortcutContext
  ): Unit = {
    val permalink = ctx
      .client()
      .chatGetPermalink(
        ChatGetPermalinkRequest
          .builder()
          .channel(req.getPayload.getChannel.getId)
          .messageTs(req.getPayload.getMessageTs)
          .build()
      )
      .getPermalink

    storeRepository.createMostRecentMessageLink(
      req.getPayload.getTeam.getId,
      req.getPayload.getUser.getId,
      permalink
    )
  }

  private def createRegistrationAuthInfo(
      req: ViewSubmissionRequest,
      ctx: ViewSubmissionContext
  ): Unit = {
    def getUser = req.getPayload.getUser
    val apiKey = req.getPayload.getView.getState.getValues
      .get(INPUT_API_KEY_BLOCK)
      .get(INPUT_API_KEY_ACTION)
      .getValue
    val spaceId = req.getPayload.getView.getState.getValues
      .get(INPUT_SPACE_ID_BLOCK)
      .get(INPUT_SPACE_ID_ACTION)
      .getValue
    // TODO: 登録する前に以下で認証情報の確認
    //  https://developer.nulab.com/ja/docs/backlog/api/2/get-own-user/#
    storeRepository.createBacklogAuthInfo(
      getUser.getTeamId,
      getUser.getId,
      apiKey,
      spaceId
    )
  }

  private def getInputIssueInfoViewBuilder(
      r: ViewsUpdateRequestBuilder,
      req: BlockActionRequest,
      options: util.List[OptionObject]
  ): ViewsUpdateRequestBuilder = {
    r.view(getInputIssueInfoView(options)).viewId(req.getPayload.getView.getId)
  }

  private def getInputIssueInfoView(options: util.List[OptionObject]): View = {
    View
      .builder()
      .`type`("modal")
      .callbackId(SlackEventTypes.RegistrationIssueToBacklog.typeName)
      .title(viewTitle(vt => vt.`type`("plain_text").text("課題を登録する")))
      .close(viewClose(c => c.`type`("plain_text").text("閉じる")))
      .submit(
        viewSubmit((submit: ViewSubmit.ViewSubmitBuilder) =>
          submit.`type`("plain_text").text("送信").emoji(true)
        )
      )
      .blocks(
        asBlocks(
          input(i =>
            i.element(
              staticSelect(ss =>
                ss.actionId(SELECT_ISSUE_TYPE_ACTION)
                  .options(
                    options
                  )
                  .placeholder(
                    PlainTextObject.builder().text("課題種別を選択してください").build()
                  )
              )
            ).label(PlainTextObject.builder().text("課題種別").build())
              .blockId(SELECT_ISSUE_TYPE_BLOCK)
          ),
          input(i =>
            i.element(
              plainTextInput(pt =>
                pt.actionId(SELECT_ISSUE_TITLE_ACTION)
                  .placeholder(
                    PlainTextObject.builder().text("タイトルを入力してください").build()
                  )
              )
            ).label(PlainTextObject.builder().text("タイトル").build())
              .blockId(SELECT_ISSUE_TITLE_BLOCK)
          )
        )
      )
      .build()
  }
  private def getSelectProjectViewBuilder(
      r: ViewsOpenRequestBuilder,
      req: MessageShortcutRequest,
      options: util.List[OptionObject]
  ): ViewsOpenRequestBuilder = {
    r.triggerId(req.getPayload.getTriggerId)
      .view(getSelectProjectView(options))
  }
  private def getSelectProjectView(options: util.List[OptionObject]): View = {
    View
      .builder()
      .`type`("modal")
      .callbackId(SlackEventTypes.PostIssueInfoReqToSlack.typeName)
      .title(viewTitle(vt => vt.`type`("plain_text").text("プロジェクトを選択")))
      .close(viewClose(c => c.`type`("plain_text").text("閉じる")))
      .blocks(
        asBlocks(
          section(s =>
            s.accessory(
              staticSelect(s =>
                s.actionId(SlackEventTypes.PostIssueInfoReqToSlack.typeName)
                  .options(options)
              )
            ).text(PlainTextObject.builder().text("プロジェクトを選択してください").build())
              .blockId(SELECT_PROJECT_BLOCK)
          )
        )
      )
      .build()
  }

  private def getInputAuthInfoViewBuilder(
      r: ViewsOpenRequestBuilder,
      req: MessageShortcutRequest
  ): ViewsOpenRequestBuilder = {
    r.triggerId(req.getPayload.getTriggerId)
      .view(
        view(v =>
          v
            .`type`("modal")
            .callbackId(SlackEventTypes.RegistrationAuthInfoToStore.typeName)
            .title(viewTitle(vt => vt.`type`("plain_text").text("認証情報を入力する")))
            .close(viewClose(c => c.`type`("plain_text").text("閉じる")))
            .submit(
              viewSubmit((submit: ViewSubmit.ViewSubmitBuilder) =>
                submit.`type`("plain_text").text("送信").emoji(true)
              )
            )
            .blocks(
              asBlocks(
                input(i =>
                  i.element(
                    plainTextInput(pt =>
                      pt.actionId(INPUT_API_KEY_ACTION)
                        .placeholder(
                          PlainTextObject
                            .builder()
                            .text("APIキーを入力してください")
                            .build()
                        )
                    )
                  ).label(PlainTextObject.builder().text("APIキー").build())
                    .blockId(INPUT_API_KEY_BLOCK)
                ),
                input(i =>
                  i.element(
                    plainTextInput(pt =>
                      pt.actionId(INPUT_SPACE_ID_ACTION)
                        .placeholder(
                          PlainTextObject
                            .builder()
                            .text("スペースIDを入力してください")
                            .build()
                        )
                    )
                  ).label(PlainTextObject.builder().text("スペースID").build())
                    .blockId(INPUT_SPACE_ID_BLOCK)
                )
              )
            )
        )
      )
  }

  private def getCreatedIssueInfoView(url: String): View = {
    View
      .builder()
      .`type`("modal")
      .title(viewTitle(vt => vt.`type`("plain_text").text("課題の登録に成功しました")))
      .close(viewClose(c => c.`type`("plain_text").text("閉じる")))
      .blocks(
        asBlocks(
          section(s => s.text(markdownText(s"$url")))
        )
      )
      .build()
  }

  private def getProjectOptions(
      authInfoEntity: BacklogAuthInfoParams
  ): util.ArrayList[OptionObject] = {
    val projects = backlogRepository.getProjects(authInfoEntity)
    // BuildKitに渡すために、JavaのArrayListを使用する必要あり
    val options = new java.util.ArrayList[OptionObject]()
    projects.forEach(p =>
      options.add(
        OptionObject
          .builder()
          .value(p.getId.toString)
          .text(PlainTextObject.builder().text(p.getName).build())
          .build()
      )
    )
    options
  }

  private def getIssueTypes(
      authInfoEntity: BacklogAuthInfoParams,
      projectId: String
  ): util.ArrayList[OptionObject] = {
    val projects = backlogRepository.getIssueTypes(authInfoEntity, projectId)
    // BuildKitに渡すために、JavaのArrayListを使用する必要あり
    val options = new java.util.ArrayList[OptionObject]()
    projects.forEach(p =>
      options.add(
        OptionObject
          .builder()
          .value(
            PROJECT_ID + ":" + p.getProjectId.toString + "," + ISSUE_TYPE_ID + ":" + p.getId.toString
          )
          .text(PlainTextObject.builder().text(p.getName).build())
          .build()
      )
    )
    options
  }
  /*
   * === private method 終了===
   */
}
