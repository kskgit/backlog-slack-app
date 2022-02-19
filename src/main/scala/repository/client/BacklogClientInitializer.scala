package repository.client

import com.nulabinc.backlog4j.{BacklogClient, BacklogClientFactory}
import com.nulabinc.backlog4j.conf.{BacklogComConfigure, BacklogConfigure}
import entity.BacklogAuthInfoEntity

// TODO: クライアントのメソッドをラップしたメソッド作成
case class BacklogClientInitializer() {
  def initialize(backlogAuthInfoEntity:BacklogAuthInfoEntity): BacklogClient = {
    val configure: BacklogConfigure = new BacklogComConfigure(backlogAuthInfoEntity.spaceId).apiKey(backlogAuthInfoEntity.apiKey)
    new BacklogClientFactory(configure).newClient
  }
}