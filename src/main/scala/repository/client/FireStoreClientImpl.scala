package repository.client

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.{FirebaseApp, FirebaseOptions}

/** FirestoreClientライブラリのメソッドをラップして提供する */
case class FireStoreClientImpl() {

  /* NOTE クライアントの初期化処理について
   * 以下コンパニオンオブジェクトでの初期化処理を検討するも失敗したためクラスに直書き
   *object FireStoreClientImpl {
   *  def apply(): FirebaseApp = {
   *    val credentials = GoogleCredentials.fromStream(
   *      getClass.getClassLoader.getResourceAsStream(
   *        sys.env("FIREBASE_SETTING_JSON")
   *      )
   *    )
   *    val options =
   *      FirebaseOptions.builder().setCredentials(credentials).build
   *
   *    FirebaseApp.initializeApp(options)
   *  }
   *}
   */

  /* initialize client start
   *
   */
  private val credentials = GoogleCredentials.fromStream(
    getClass.getClassLoader.getResourceAsStream(
      sys.env("FIREBASE_SETTING_JSON")
    )
  )
  private val options =
    FirebaseOptions.builder().setCredentials(credentials).build
  FirebaseApp.initializeApp(options)
  /*
   *
   * initialize client  end */

  private val fireStore: Firestore = FirestoreClient.getFirestore

  /** コレクション -> ドキュメント 配下の値をキーを指定して取得する
    *
    * @param collectionName コレクション名
    * @param documentName ドキュメント名
    * @param key キー名
    * @return 取得した値、値が無ければ空文字
    */
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

  /** コレクション -> ドキュメント 配下に値を保存する
    *
    * @param collectionName コレクション名
    * @param documentName ドキュメント名
    * @return 取得した値、値が無ければ空文字
    */
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
