package repository.impl

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.google.firebase.cloud.FirestoreClient
import entity.BacklogInfo
import repository.FireStoreRepository

import java.io.FileInputStream
import scala.collection.mutable

case class FireStoreRepositoryImpl() extends FireStoreRepository {

  val serviceAccount = new FileInputStream("/Users/ksk/Downloads/slack-to-backlog-933bd-firebase-adminsdk-t3lgn-6a2b4af488.json")
  val credentials: GoogleCredentials = GoogleCredentials.fromStream(serviceAccount);

  val options: FirebaseOptions = FirebaseOptions.builder().setCredentials(credentials).build
  FirebaseApp.initializeApp(options)

  override def getBacklogKey(userId: String): BacklogInfo = {
    val db = FirestoreClient.getFirestore
    // TODO: ハードコーディング修正（FireStoreKeyなど作る？？）
    val query = db.collection("users").get
    val querySnapshot = query.get
    val documents = querySnapshot.getDocuments

    val m = new mutable.HashMap[String, String]
   documents.forEach(d => if (d.getId == userId) {
      m.addOne("spaceId" -> d.getData.get("spaceId").toString)
      m.addOne("apiKey" -> d.getData.get("apiKey").toString)
    })

    BacklogInfo.apply(m.getOrElse("spaceId", ""), m.getOrElse("apiKey", ""))
  }

  override def setBacklogKey(userId: String, spaceId: String, apiKey: String): Unit = {
    // TODO: 実装 collectionをSlackのUserIdにする
  }
}
