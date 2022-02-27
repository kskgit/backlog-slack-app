package repository.impl

import org.mockito.Mockito.{times, verify, when}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import params.BacklogAuthInfoParams
import repository.client.FireStoreClientImpl

import java.util

class FireStoreRepositoryImplSpec extends AnyFunSuite with MockitoSugar {
  test("getBacklogAuthInfo 認証情報登録有り") {
    val fireStoreClientImpl = mock[FireStoreClientImpl]
    when(
      fireStoreClientImpl.getValInCollectionDocument(
        "users",
        "teamId",
        "userId"
      )
    ) thenReturn "{spaceId=space-id, apiKey=apiKey}"

    val result = FireStoreRepositoryImpl(fireStoreClientImpl)
      .getBacklogAuthInfo("teamId", "userId")

    val expect = BacklogAuthInfoParams.apply("space-id", "apiKey")
    assert(result == expect)
  }

  test("getBacklogAuthInfo 認証情報登録無し") {
    val fireStoreClientImpl = mock[FireStoreClientImpl]
    when(
      fireStoreClientImpl.getValInCollectionDocument(
        "users",
        "teamId",
        "userId"
      )
    ) thenReturn ""

    val result = FireStoreRepositoryImpl(fireStoreClientImpl)
      .getBacklogAuthInfo("teamId", "userId")

    val expect = BacklogAuthInfoParams.apply("", "")
    assert(result == expect)
  }

  test("createBacklogAuthInfo") {
    val fireStoreClientImpl = mock[FireStoreClientImpl]
    val tmpAuthInfo = new util.HashMap[String, String] {
      {
        put("spaceId", "space-id")
        put("apiKey", "apiKey")
      }
    }
    val param = new util.HashMap[String, util.HashMap[String, String]] {
      {
        put("userId", tmpAuthInfo)
      }
    }

    FireStoreRepositoryImpl(fireStoreClientImpl)
      .createBacklogAuthInfo("teamId", "userId", "apiKey", "space-id")

    verify(fireStoreClientImpl, times(1)).createValInCollectionDocument(
      "users",
      "teamId",
      param
    )
  }

  test("createMostRecentMessageLink") {
    val fireStoreClientImpl = mock[FireStoreClientImpl]
    val param = new util.HashMap[String, String] {
      {
        put("userId", "url")
      }
    }

    FireStoreRepositoryImpl(fireStoreClientImpl)
      .createMostRecentMessageLink("teamId", "userId", "url")

    verify(fireStoreClientImpl, times(1)).createValInCollectionDocument(
      "links",
      "teamId",
      param
    )
  }

  test("getMostRecentMessageLink") {
    val fireStoreClientImpl = mock[FireStoreClientImpl]

    FireStoreRepositoryImpl(fireStoreClientImpl)
      .getMostRecentMessageLink("teamId", "userId")

    verify(fireStoreClientImpl, times(1)).getValInCollectionDocument(
      "links",
      "teamId",
      "userId"
    )
  }

}
