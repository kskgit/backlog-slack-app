package repository.impl

import com.slack.api.bolt.request.builtin.MessageShortcutRequest
import entity.BacklogAuthInfoEntity
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import repository.client.BacklogClientInitializer

class BacklogRepositoryImplSpec extends AnyFunSuite with MockitoSugar  {
  test("createIssue テスト") {
    val backlogClientInitializer = mock[BacklogClientInitializer](Mockito.RETURNS_DEEP_STUBS)
    val messageShortcutRequest = mock[MessageShortcutRequest](Mockito.RETURNS_DEEP_STUBS)
    val backlogAuthInfoEntity = mock[BacklogAuthInfoEntity](Mockito.RETURNS_DEEP_STUBS)

    val backlogRepositoryImpl = BacklogRepositoryImpl.apply(backlogClientInitializer)
    backlogRepositoryImpl.createIssue(messageShortcutRequest, backlogAuthInfoEntity)

    verify(backlogClientInitializer, times(1)).initialize(backlogAuthInfoEntity)
    // TODO: createIssueのパラメータをワイルドカードで出来ないか確認
    //    verify(backlogClientInitializer.initialize(backlogAuthInfoEntity), times(1)).createIssue(ここ！！)
  }
}
