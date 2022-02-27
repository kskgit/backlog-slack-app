package repository.client

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.{FirebaseApp, FirebaseOptions}

/** FirestoreClientのメソッドをラップして提供するクラス
  */
case class FireStoreClientImpl() {

  // NOTE クライアントの初期化処理について
  // 以下コンパニオンオブジェクトでの初期化処理を検討するも失敗したためクラスに直書き
  //object FireStoreClientImpl {
  //  def apply(): FirebaseApp = {
  //    val credentials = GoogleCredentials.fromStream(
  //      getClass.getClassLoader.getResourceAsStream(
  //        sys.env("FIREBASE_SETTING_JSON")
  //      )
  //    )
  //    val options =
  //      FirebaseOptions.builder().setCredentials(credentials).build
  //
  //    FirebaseApp.initializeApp(options)
  //  }
  //}

  //
  //  クライアント初期化処理 開始===
  //
  private val credentials = GoogleCredentials.fromStream(
    getClass.getClassLoader.getResourceAsStream(
      sys.env("FIREBASE_SETTING_JSON")
    )
  )
  private val options =
    FirebaseOptions.builder().setCredentials(credentials).build
  FirebaseApp.initializeApp(options)
  //  クライアント初期化処理 終了===

  private val fireStore: Firestore = FirestoreClient.getFirestore

  def getValInCollectionDocument(
      collectionName: String,
      documentName: String,
      key: String
  ): String = {
    val value = fireStore
      .collection(collectionName)
      .document(documentName)
      .get()
      .get()
      .get(key)

    if (value != null) {
      value.toString
    } else {
      ""
    }
  }

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
