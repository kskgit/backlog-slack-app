package repository.impl

import org.mockito.Mockito
import org.mockito.Mockito.{times, verify}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import params.BacklogAuthInfoParams
import repository.client.BacklogClientImpl

class BacklogRepositoryImplSpec extends AnyFunSuite with MockitoSugar {
  test("createIssue テスト") {
    val backlogClient = mock[BacklogClientImpl](Mockito.RETURNS_DEEP_STUBS)
    val backlogAuthInfoParams =
      mock[BacklogAuthInfoParams](Mockito.RETURNS_DEEP_STUBS)
    val backlogRepositoryImpl = BacklogRepositoryImpl(backlogClient)
    val createIssueParams =
      backlogRepositoryImpl.getCreateIssueParams("1", "1", 1, "https://xxx.com")

    backlogRepositoryImpl.createIssue(createIssueParams, backlogAuthInfoParams)

    verify(backlogClient, times(1))
      .createIssue(createIssueParams, backlogAuthInfoParams)
  }
}
