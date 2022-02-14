import com.nulabinc.backlog4j.{ResponseList, User}
import org.scalatest.funsuite.AnyFunSuite

class BacklogRepositorySpec  extends AnyFunSuite {
  test("An empty Set should have size 0") {
    assert(BacklogRepository.getUsers == List[User]())
  }
}
