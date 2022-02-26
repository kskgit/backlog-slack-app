package repository.impl

import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import entity.BacklogAuthInfoEntity
import params.StoreParams
import repository.StoreRepository
import repository.client.FireStoreClientImpl

import java.util
import javax.inject.Inject

// TODO: ハードコーディング修正（FireStoreの構造に依存する = FireStoreRepositoryImplに依存するため、ここにstatic valueとして記載する）
case class FireStoreRepositoryImpl @Inject() (
    fireStoreClient: FireStoreClientImpl
) extends StoreRepository {
  //  users collection keys
  private final val users = "users"
  private final val spaceId = "spaceId"
  private final val apiKey = "apiKey"
  //  links collection keys
  private final val links = "links"

  private val db = fireStoreClient.fireStore

  // TODO: getMostRecentMessageLinkを参考にリファクタ
  override def getBacklogAuthInfo(
      teamId: String,
      userId: String
  ): BacklogAuthInfoEntity = {

    val authInfo =
      fireStoreClient.getValInCollectionDocument(users, teamId, userId)

    if (authInfo == null) {
      return BacklogAuthInfoEntity.apply("", "")
    }

    val authInfoMap = authInfo
      .substring(1, authInfo.length - 1)
      .replace(" ", "")
      .split(",")
      .map(_.split("="))
      .map { case Array(k, v) => (k, v) }
      .toMap

    BacklogAuthInfoEntity.apply(authInfoMap(spaceId), authInfoMap(apiKey))
  }

  override def createBacklogAuthInfo(
      teamId: String,
      userId: String,
      req: ViewSubmissionRequest
  ): Unit = {
    val apiKey = req.getPayload.getView.getState.getValues
      .get("apiBlock")
      .get("apiAction")
      .getValue
    val spaceId = req.getPayload.getView.getState.getValues
      .get("spaceBlock")
      .get("spaceAction")
      .getValue
    // JavaのhashMapを渡す必要あり
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
    db.collection(users).document(teamId).set(param).get()
  }

  override def createMostRecentMessageLink(
      teamId: String,
      userId: String,
      url: String
  ): Unit = {
    // JavaのhashMapを渡す必要あり
    val param = new util.HashMap[String, String] {
      {
        put(userId, url)
      }
    }
    db.collection(links).document(teamId).set(param).get()
  }

  override def getMostRecentMessageLink(
      teamId: String,
      userId: String
  ): String = {
    val query = db.collection(links).document(teamId)
    val doc = query.get().get().get(userId)
    doc.toString
  }
}
