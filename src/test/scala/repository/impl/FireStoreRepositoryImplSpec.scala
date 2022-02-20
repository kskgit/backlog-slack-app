package repository.impl

import entity.BacklogAuthInfoEntity
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import repository.client.FireStoreClientInitialized

class FireStoreRepositoryImplSpec extends AnyFunSuite with MockitoSugar {
  test("getBacklogKey テスト") {
    val result = FireStoreRepositoryImpl(FireStoreClientInitialized()).getBacklogAuthInfo("channelId","userId")
    // TODO: テストコード見直し
    val expect = BacklogAuthInfoEntity.apply("test", "test")
    assert(result == expect)
  }

  test("setBacklogKey テスト") {
    val fireStoreClient = FireStoreClientInitialized()
    val fireStoreRepositoryImpl = FireStoreRepositoryImpl(fireStoreClient)
    // TODO: テストコード見直し
    fireStoreRepositoryImpl.setBacklogAuthInfo("channelId","userId","spaceId","apiKey")
//    val expect = BacklogAuthInfoEntity.apply("test", "test")
//    assert(result == expect)
  }

}
