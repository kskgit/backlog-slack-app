package endpoint.impl

import com.slack.api.bolt.context.builtin.MessageShortcutContext
import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import params.BacklogAuthInfoParams
import repository.client.FireStoreClientImpl
import repository.impl.{BacklogRepositoryImpl, FireStoreRepositoryImpl}
import service.impl.SlackEventHandleServiceImpl

class SlackEventHandleServiceImplSpec extends AnyFunSuite with MockitoSugar {
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

    //
    // 処理実施
    //
    SlackEventHandleServiceImpl(backlogRepository, storeRepository)()
      .acceptCreateIssueRequest(req, ctx)

    //
    // 結果確認
    //
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

    //
    // 処理実施
    //
    SlackEventHandleServiceImpl(backlogRepository, storeRepository)()
      .acceptCreateIssueRequest(req, ctx)

    //
    // 結果確認
    //
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
// TODO postIssueInfoReqToSlack、registrationAuthInfoToStore、registrationIssueToBacklogテスト
}
