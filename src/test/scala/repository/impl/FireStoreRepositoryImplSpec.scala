package repository.impl

import entity.BacklogAuthInfoEntity
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import repository.client.FireStoreClientInitialized

class FireStoreRepositoryImplSpec extends AnyFunSuite with MockitoSugar {
  test("getBacklogAuthInfo テスト") {
    val result = FireStoreRepositoryImpl(FireStoreClientInitialized()).getBacklogAuthInfo("XXX","YYY")
    // TODO: テストコード見直し
    val expect = BacklogAuthInfoEntity.apply("test", "test")
    assert(result == expect)
  }

  test("setBacklogKey テスト") {
    val fireStoreClient = FireStoreClientInitialized()
    val fireStoreRepositoryImpl = FireStoreRepositoryImpl(fireStoreClient)
    // TODO: テストコード見直し
//    fireStoreRepositoryImpl.setBacklogAuthInfo("channelId","userId","spaceId","apiKey")
//    val expect = BacklogAuthInfoEntity.apply("test", "test")
//    assert(result == expect)
  }

  test("createMostRecentMessageLink テスト") {
    val fireStoreClient = FireStoreClientInitialized()
    val fireStoreRepositoryImpl = FireStoreRepositoryImpl(fireStoreClient)
    // TODO: テストコード見直し
    fireStoreRepositoryImpl.createMostRecentMessageLink("teamId","userId","url")
//    val expect = BacklogAuthInfoEntity.apply("test", "test")
//    assert(result == expect)
  }

  test("getMostRecentMessageLink テスト") {
    val fireStoreClient = FireStoreClientInitialized()
    val fireStoreRepositoryImpl = FireStoreRepositoryImpl(fireStoreClient)
    // TODO: テストコード見直し
    fireStoreRepositoryImpl.getMostRecentMessageLink("teamId","userId")
    //    val expect = BacklogAuthInfoEntity.apply("test", "test")
    //    assert(result == expect)
  }
}
