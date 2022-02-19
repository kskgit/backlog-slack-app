package repository.client

import com.nulabinc.backlog4j.{BacklogClient, BacklogClientFactory}
import com.nulabinc.backlog4j.conf.{BacklogComConfigure, BacklogConfigure}
import entity.BacklogAuthInfoEntity

case class BacklogClientInitializer() {
  def initialize(backlogAuthInfoEntity:BacklogAuthInfoEntity): BacklogClient = {
    val configure: BacklogConfigure = new BacklogComConfigure(backlogAuthInfoEntity.spaceId).apiKey(backlogAuthInfoEntity.apiKey)
    new BacklogClientFactory(configure).newClient
  }
}
//sys.env("BACKLOG_SPACE_ID")
//sys.env("BACKLOG_API_KEY")