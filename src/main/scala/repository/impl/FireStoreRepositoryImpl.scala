package repository.impl
import params.BacklogAuthInfoParams
import repository.StoreRepository
import repository.client.FireStoreClientImpl

import java.util
import javax.inject.Inject

case class FireStoreRepositoryImpl @Inject() (
    fireStoreClient: FireStoreClientImpl
) extends StoreRepository {
  /*
   * NOTE:FireStoreのコレクション名はFireStoreに依存するためこのクラスに記載
   */
  private final val USERS = "users"
  private final val SPACE_ID = "spaceId"
  private final val APIKEY = "apiKey"
  //  links collection keys
  private final val LINKS = "links"

  override def getBacklogAuthInfo(
      teamId: String,
      userId: String
  ): BacklogAuthInfoParams = {
    val authInfo =
      fireStoreClient.getValInCollectionDocument(USERS, teamId, userId)

    if (authInfo.isEmpty) {
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

    BacklogAuthInfoParams.apply(authInfoMap(SPACE_ID), authInfoMap(APIKEY))
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
        // TODO: 保存時に暗号化
        put(SPACE_ID, spaceId)
        put(APIKEY, apiKey)
      }
    }
    val param = new util.HashMap[String, util.HashMap[String, String]] {
      {
        put(userId, tmpAuthInfo)
      }
    }
    fireStoreClient.createValInCollectionDocument(USERS, teamId, param)
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
    fireStoreClient.createValInCollectionDocument(LINKS, teamId, param)
  }

  override def getMostRecentMessageLink(
      teamId: String,
      userId: String
  ): String = {
    fireStoreClient.getValInCollectionDocument(LINKS, teamId, userId)
  }
}
