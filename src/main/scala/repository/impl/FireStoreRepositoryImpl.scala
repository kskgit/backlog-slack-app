package repository.impl
import params.BacklogAuthInfoParams
import repository.StoreRepository
import repository.client.FireStoreClientImpl

import java.util
import javax.inject.Inject

/** FireStoreとのやり取りを提供するクラス
  * @param fireStoreClient 公式ライブラリFirestoreClientをラップしたクラス
  */
case class FireStoreRepositoryImpl @Inject() (
    fireStoreClient: FireStoreClientImpl
) extends StoreRepository {
  // NOTE:FireStoreのコレクション名はFireStoreに依存するためこのクラスに記載
  //  users collection keys
  private final val users = "users"
  private final val spaceId = "spaceId"
  private final val apiKey = "apiKey"
  //  links collection keys
  private final val links = "links"

  private val db = fireStoreClient.fireStore

  override def getBacklogAuthInfo(
      teamId: String,
      userId: String
  ): BacklogAuthInfoParams = {
    val authInfo =
      fireStoreClient.getValInCollectionDocument(users, teamId, userId)

    if (authInfo == null) {
      return BacklogAuthInfoParams.apply("", "")
    }

    // FireStoreからStringで取得した値をMapに加工
    val authInfoMap = authInfo
      .substring(1, authInfo.length - 1)
      .replace(" ", "")
      .split(",")
      .map(_.split("="))
      .map { case Array(k, v) => (k, v) }
      .toMap

    BacklogAuthInfoParams.apply(authInfoMap(spaceId), authInfoMap(apiKey))
  }

  override def createBacklogAuthInfo(
      teamId: String,
      userId: String,
      apiKey: String,
      spaceId: String
  ): Unit = {
    // FireStoreへ渡す値を加工、JavaのhashMapを渡す必要あり
    val tmpAuthInfo = new util.HashMap[String, String] {
      {
        put("spaceId", spaceId)
        put("apiKey", apiKey)
      }
    }
    val param = new util.HashMap[String, util.HashMap[String, String]] {
      {
        put(userId, tmpAuthInfo)
      }
    }
    fireStoreClient.createValInCollectionDocument(users, teamId, param)
  }

  override def createMostRecentMessageLink(
      teamId: String,
      userId: String,
      url: String
  ): Unit = {
    // FireStoreへ渡す値を加工、JavaのhashMapを渡す必要あり
    val param = new util.HashMap[String, String] {
      {
        put(userId, url)
      }
    }
    fireStoreClient.createValInCollectionDocument(links, teamId, param)
  }

  override def getMostRecentMessageLink(
      teamId: String,
      userId: String
  ): String = {
    fireStoreClient.getValInCollectionDocument(links, teamId, userId)
  }
}
