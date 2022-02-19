package repository.impl

import entity.BacklogAuthInfoEntity
import repository.StoreRepository
import repository.client.FireStoreClientInitialized

import javax.inject.Inject
import scala.collection.mutable

case class FireStoreRepositoryImpl @Inject() (fireStoreClient: FireStoreClientInitialized) extends StoreRepository {

  private val db = fireStoreClient.fireStore

  override def getBacklogAuthInfo(channelId :String, userId: String): BacklogAuthInfoEntity = {
    // TODO: ハードコーディング修正（case class FireStoreKeyなど作る？？）
    val query = db.collection("users").get
    val querySnapshot = query.get
    val documents = querySnapshot.getDocuments

    val m = new mutable.HashMap[String, String]
    documents.forEach(d => if (d.getId == userId) {
      m.addOne("spaceId" -> d.getData.get("spaceId").toString)
      m.addOne("apiKey" -> d.getData.get("apiKey").toString)
    })

    BacklogAuthInfoEntity.apply(m.getOrElse("spaceId", ""), m.getOrElse("apiKey", ""))
  }

  override def setBacklogAuthInfo(channelId :String, userId: String, spaceId: String, apiKey: String): Unit = {
    // TODO: 実装 collectionをSlackのUserIdにする
  }
}
