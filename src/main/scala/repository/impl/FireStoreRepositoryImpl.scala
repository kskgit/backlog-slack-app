package repository.impl

import entity.BacklogAuthInfoEntity
import repository.StoreRepository
import repository.client.FireStoreClientInitialized

import java.util
import javax.inject.Inject
import scala.collection.mutable

case class FireStoreRepositoryImpl @Inject() (fireStoreClient: FireStoreClientInitialized) extends StoreRepository {

  private val db = fireStoreClient.fireStore

  override def getBacklogAuthInfo(channelId :String, userId: String): BacklogAuthInfoEntity = {
    // TODO: ハードコーディング修正（case class FireStoreKeyなど作る？？）
    val query = db.collection("users").get()
    val querySnapshot = query.get
    val documents = querySnapshot.getDocuments

    // TODO: 関数型な書き方を検討する
    val m = new mutable.HashMap[String, String]
    documents.forEach(d => if (d.getId == userId) {
      m.addOne("spaceId" -> d.getData.get("spaceId").toString)
      m.addOne("apiKey" -> d.getData.get("apiKey").toString)
    })

    BacklogAuthInfoEntity.apply(m.getOrElse("spaceId", ""), m.getOrElse("apiKey", ""))
  }

  override def setBacklogAuthInfo(channelId :String, userId: String, spaceId: String, apiKey: String): Unit = {
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
    db.collection("users").document(channelId).set(param).get()
  }
}
