package repository.impl

import entity.BacklogAuthInfoEntity
import org.mockito.Mockito.when
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar
//import org.mockito.Mockito.when
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import repository.client.FireStoreClientImpl

class FireStoreRepositoryImplSpec extends AnyFunSuite with MockitoSugar {
  test("getBacklogAuthInfo 認証情報登録有り") {
    val fireStoreClientImpl = mock[FireStoreClientImpl]
    when(
      fireStoreClientImpl.getValInCollectionDocument(
        "users",
        "teamId",
        "userId"
      )
    ) thenReturn "{spaceId=project-id, apiKey=abC123}"

    val result = FireStoreRepositoryImpl(fireStoreClientImpl)
      .getBacklogAuthInfo("teamId", "userId")

    val expect = BacklogAuthInfoEntity.apply("project-id", "abC123")
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
    ) thenReturn null

    val result = FireStoreRepositoryImpl(fireStoreClientImpl)
      .getBacklogAuthInfo("teamId", "userId")

    val expect = BacklogAuthInfoEntity.apply("", "")
    assert(result == expect)
  }
  //
//  test("getBacklogAuthInfo テスト") {
//    val result = FireStoreRepositoryImpl(FireStoreClientImpl())
//      .getBacklogAuthInfo("XXX", "YYY")
//    // TODO: テストコード見直し
//    val expect = BacklogAuthInfoEntity.apply("test", "test")
//    assert(result == expect)
//  }
//
//  test("setBacklogKey テスト") {
//    val fireStoreClient = FireStoreClientImpl()
//    val fireStoreRepositoryImpl = FireStoreRepositoryImpl(fireStoreClient)
//    // TODO: テストコード見直し
////    fireStoreRepositoryImpl.setBacklogAuthInfo("channelId","userId","spaceId","apiKey")
////    val expect = BacklogAuthInfoEntity.apply("test", "test")
////    assert(result == expect)
//  }
//
//  test("createMostRecentMessageLink テスト") {
//    val fireStoreClient = FireStoreClientImpl()
//    val fireStoreRepositoryImpl = FireStoreRepositoryImpl(fireStoreClient)
//    // TODO: テストコード見直し
//    fireStoreRepositoryImpl.createMostRecentMessageLink(
//      "teamId",
//      "userId",
//      "url"
//    )
////    val expect = BacklogAuthInfoEntity.apply("test", "test")
////    assert(result == expect)
//  }
//
//  test("getMostRecentMessageLink テスト") {
//    val fireStoreClient = FireStoreClientImpl()
//    val fireStoreRepositoryImpl = FireStoreRepositoryImpl(fireStoreClient)
//    // TODO: テストコード見直し
//    fireStoreRepositoryImpl.getMostRecentMessageLink("teamId", "userId")
//    //    val expect = BacklogAuthInfoEntity.apply("test", "test")
//    //    assert(result == expect)
}
