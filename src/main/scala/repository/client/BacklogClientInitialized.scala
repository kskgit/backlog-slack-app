package repository.client

import com.nulabinc.backlog4j.{BacklogClient, BacklogClientFactory}
import com.nulabinc.backlog4j.conf.{BacklogComConfigure, BacklogConfigure}

case class BacklogClientInitialized() {
  private val configure: BacklogConfigure = new BacklogComConfigure(sys.env("BACKLOG_SPACE_ID")).apiKey(sys.env("BACKLOG_API_KEY"))
  val backlog: BacklogClient = new BacklogClientFactory(configure).newClient
}
