package repository.client

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.{DocumentReference, Firestore}
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.{FirebaseApp, FirebaseOptions}

/** FirestoreClientのメソッドをラップして提供するクラス
  */
case class FireStoreClientImpl() {

  // TODO: コンパニオンオブジェクトの中に記載するか検討
  private val credentials = GoogleCredentials.fromStream(
    getClass.getClassLoader.getResourceAsStream(
      sys.env("FIREBASE_SETTING_JSON")
    )
  )
  private val options =
    FirebaseOptions.builder().setCredentials(credentials).build
  FirebaseApp.initializeApp(options)

  // TODO: 最後にprivateにする
  val fireStore: Firestore = FirestoreClient.getFirestore

  def getValInCollectionDocument(
      collectionName: String,
      documentName: String,
      key: String
  ): String =
    fireStore
      .collection(collectionName)
      .document(documentName)
      .get()
      .get()
      .get(key)
      .toString

  def createValInCollectionDocument(
      collectionName: String,
      documentName: String,
      value: Object
  ): Unit =
    fireStore
      .collection(collectionName)
      .document(documentName)
      .set(value)
      .get()

}
