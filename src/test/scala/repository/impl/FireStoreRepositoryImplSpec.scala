package repository.impl

import entity.BacklogInfo
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar

class FireStoreRepositoryImplSpec extends AnyFunSuite with MockitoSugar {
  test("FireStoreRepositoryImpl テスト") {
    val result = FireStoreRepositoryImpl().getBacklogKey("userId")
    // TODO: テストコード見直し 
    val expect = BacklogInfo.apply("test", "test")
    assert(result == expect)
  }
}
