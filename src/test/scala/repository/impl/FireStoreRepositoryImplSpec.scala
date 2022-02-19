package repository.impl

import entity.BacklogInfoEntity
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import repository.client.FireStoreClient

class FireStoreRepositoryImplSpec extends AnyFunSuite with MockitoSugar {
  test("getBacklogKey テスト") {
    val result = FireStoreRepositoryImpl(FireStoreClient()).getBacklogKey("channelId","userId")
    // TODO: テストコード見直し
    val expect = BacklogInfoEntity.apply("test", "test")
    assert(result == expect)
  }

  test("setBacklogKey テスト") {
//    val fireStore = FireStoreRepositoryImpl()
//    val result = FireStoreRepositoryImpl().setBacklogKey("channelId", "userId", "spaceId", "apiKey")
//    // TODO: テストコード見直し
//    val expect = BacklogInfoEntity.apply("test", "test")
//    assert(result == expect)
  }

}
