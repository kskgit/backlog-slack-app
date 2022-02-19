package repository.impl

import com.slack.api.bolt.request.builtin.{MessageShortcutRequest, ViewSubmissionRequest}
import entity.BacklogAuthInfoEntity
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import repository.client.BacklogClientInitializer

class BacklogRepositoryImplSpec extends AnyFunSuite with MockitoSugar  {
  test("createIssue テスト") {
    val backlogClientInitializer = mock[BacklogClientInitializer](Mockito.RETURNS_DEEP_STUBS)
    val messageShortcutRequest = mock[ViewSubmissionRequest](Mockito.RETURNS_DEEP_STUBS)
    val backlogAuthInfoEntity = mock[BacklogAuthInfoEntity](Mockito.RETURNS_DEEP_STUBS)

    val backlogRepositoryImpl = BacklogRepositoryImpl.apply(backlogClientInitializer)
    backlogRepositoryImpl.createIssue(messageShortcutRequest, backlogAuthInfoEntity)

    verify(backlogClientInitializer, times(1)).initialize(backlogAuthInfoEntity)
    verify(backlogClientInitializer.initialize(backlogAuthInfoEntity), times(1)).createIssue(any())
  }
}
