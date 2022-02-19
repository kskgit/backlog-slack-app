package repository.client

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.{FirebaseApp, FirebaseOptions}

import java.io.FileInputStream

// TODO: クライアントのメソッドをラップしたメソッド作成
case class FireStoreClientInitialized() {

  // TODO: コンパニオンオブジェクトの中に記載するか検討
  private val serviceAccount = new FileInputStream("/Users/ksk/Downloads/slack-to-backlog-933bd-firebase-adminsdk-t3lgn-6a2b4af488.json")
  private val credentials = GoogleCredentials.fromStream(serviceAccount)
  private val options = FirebaseOptions.builder().setCredentials(credentials).build
  FirebaseApp.initializeApp(options)

  val fireStore: Firestore = FirestoreClient.getFirestore
}
