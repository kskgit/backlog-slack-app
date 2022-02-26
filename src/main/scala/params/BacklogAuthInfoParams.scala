package params

/** BacklogAPIの使用に必要な認証情報
  *
  * @param spaceId BacklogのスペースID
  * @param apiKey BacklogのAPIキー
  */
case class BacklogAuthInfoParams(spaceId: String, apiKey: String)
