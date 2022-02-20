package repository

import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import entity.BacklogAuthInfoEntity

trait StoreRepository {
  def getBacklogAuthInfo(channelId :String, userId :String):BacklogAuthInfoEntity
  def setBacklogAuthInfo(channelId :String, userId :String, request :ViewSubmissionRequest):Unit
}
