package endpoint

/** イベントとEventHandlerを登録し処理を開始する */
trait EndPoint {
  def startServer(): Unit
}
