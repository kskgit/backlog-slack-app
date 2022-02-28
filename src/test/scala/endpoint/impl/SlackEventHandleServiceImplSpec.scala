package endpoint.impl

import com.slack.api.bolt.App
import com.slack.api.bolt.context.builtin.{
  MessageShortcutContext,
  ViewSubmissionContext
}
import com.slack.api.bolt.request.builtin.{
  MessageShortcutRequest,
  ViewSubmissionRequest
}
import com.slack.api.model.view.ViewState
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import params.BacklogAuthInfoParams
import repository.client.FireStoreClientImpl
import repository.impl.{BacklogRepositoryImpl, FireStoreRepositoryImpl}
import service.impl.SlackEventHandleServiceImpl

import java.util

class SlackEventHandleServiceImplSpec extends AnyFunSuite with MockitoSugar {
  private final val INPUT_API_KEY_BLOCK = "inputApiKeyBlock"
  private final val INPUT_API_KEY_ACTION = "inputApiKeyAction"
  private final val INPUT_SPACE_ID_BLOCK = "inputSpaceIdBlock"
  private final val INPUT_SPACE_ID_ACTION = "inputSpaceIdAction"
  test("acceptCreateIssueRequest 認証情報ありの場合") {
    val req = mock[MessageShortcutRequest](Mockito.RETURNS_DEEP_STUBS)
    when(
      req.getPayload.getTeam.getId
    ) thenReturn "teamId"
    when(
      req.getPayload.getUser.getId
    ) thenReturn "userId"
    val ctx = mock[MessageShortcutContext](Mockito.RETURNS_DEEP_STUBS)
    val fireStoreClientImpl = mock[FireStoreClientImpl]
    val storeRepository = mock[FireStoreRepositoryImpl](
      withSettings.useConstructor(fireStoreClientImpl)
    )
    when(
      storeRepository.getBacklogAuthInfo(
        "teamId",
        "userId"
      )
    ) thenReturn BacklogAuthInfoParams("space-id", "apiKey")
    val backlogRepository =
      mock[BacklogRepositoryImpl](Mockito.RETURNS_DEEP_STUBS)

    /*
     * 処理実施
     */
    SlackEventHandleServiceImpl(backlogRepository, storeRepository)()
      .acceptCreateIssueRequest(req, ctx)

    /*
     * 結果確認
     */
    verify(storeRepository, times(1)).getBacklogAuthInfo(
      "teamId",
      "userId"
    )
    verify(storeRepository, times(1)).createMostRecentMessageLink(
      any(),
      any(),
      any()
    )
    verify(backlogRepository, times(1)).getProjects(
      BacklogAuthInfoParams("space-id", "apiKey")
    )
    verify(ctx, times(1)).ack()
  }

  test("acceptCreateIssueRequest 認証情報なしの場合") {
    val req = mock[MessageShortcutRequest](Mockito.RETURNS_DEEP_STUBS)
    when(
      req.getPayload.getTeam.getId
    ) thenReturn "teamId"
    when(
      req.getPayload.getUser.getId
    ) thenReturn "userId"
    val ctx = mock[MessageShortcutContext](Mockito.RETURNS_DEEP_STUBS)
    val fireStoreClientImpl = mock[FireStoreClientImpl]
    val storeRepository = mock[FireStoreRepositoryImpl](
      withSettings.useConstructor(fireStoreClientImpl)
    )
    when(
      storeRepository.getBacklogAuthInfo(
        "teamId",
        "userId"
      )
    ) thenReturn BacklogAuthInfoParams("", "")
    val backlogRepository =
      mock[BacklogRepositoryImpl](Mockito.RETURNS_DEEP_STUBS)

    /*
     * 処理実施
     */
    SlackEventHandleServiceImpl(backlogRepository, storeRepository)()
      .acceptCreateIssueRequest(req, ctx)

    /*
     * 結果確認
     */
    verify(storeRepository, times(1)).getBacklogAuthInfo(
      "teamId",
      "userId"
    )
    verify(storeRepository, times(1)).createMostRecentMessageLink(
      any(),
      any(),
      any()
    )
    verify(backlogRepository, never()).getProjects(
      BacklogAuthInfoParams("space-id", "apiKey")
    )
    verify(ctx, times(1)).ack()
  }

  test("registrationAuthInfoToStore") {
    val req = mock[ViewSubmissionRequest](Mockito.RETURNS_DEEP_STUBS)
    when(
      req.getPayload.getTeam.getId
    ) thenReturn "teamId"
    when(
      req.getPayload.getUser.getId
    ) thenReturn "userId"
    when(
      req.getPayload.getUser.getTeamId
    ) thenReturn "teamId"

    val viewStateValueKey = mock[ViewState.Value](Mockito.RETURNS_DEEP_STUBS)
    when(
      viewStateValueKey.getValue
    ) thenReturn "apiKey"

    val viewStateKey =
      mock[util.HashMap[String, ViewState.Value]](Mockito.RETURNS_DEEP_STUBS)
    when(
      viewStateKey.get(INPUT_API_KEY_ACTION)
    ) thenReturn viewStateValueKey
    when(
      req.getPayload.getView.getState.getValues
        .get(INPUT_API_KEY_BLOCK)
    ) thenReturn viewStateKey

    val viewStateValueId = mock[ViewState.Value](Mockito.RETURNS_DEEP_STUBS)
    when(
      viewStateValueId.getValue
    ) thenReturn "spaceId"

    val viewStateId =
      mock[util.HashMap[String, ViewState.Value]](Mockito.RETURNS_DEEP_STUBS)
    when(
      viewStateId.get(INPUT_SPACE_ID_ACTION)
    ) thenReturn viewStateValueId
    when(
      req.getPayload.getView.getState.getValues
        .get(INPUT_SPACE_ID_BLOCK)
    ) thenReturn viewStateId

    val ctx = mock[ViewSubmissionContext](Mockito.RETURNS_DEEP_STUBS)
    val backlogRepository =
      mock[BacklogRepositoryImpl](Mockito.RETURNS_DEEP_STUBS)
    val fireStoreClientImpl =
      mock[FireStoreClientImpl](Mockito.RETURNS_DEEP_STUBS)
    val storeRepository = mock[FireStoreRepositoryImpl](
      withSettings.useConstructor(fireStoreClientImpl)
    )

    /*
     * 処理実施
     */
    SlackEventHandleServiceImpl(backlogRepository, storeRepository)()
      .registrationAuthInfoToStore(req, ctx)

    /*
     * 結果確認
     */
    verify(storeRepository, times(1)).createBacklogAuthInfo(
      "teamId",
      "userId",
      "apiKey",
      "spaceId"
    )
    verify(storeRepository, times(1)).getBacklogAuthInfo(
      "teamId",
      "userId"
    )

    verify(backlogRepository, times(1)).getProjects(
      any()
    )
  }
// TODO postIssueInfoReqToSlack、registrationIssueToBacklogテスト
}
