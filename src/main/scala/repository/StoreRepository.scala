package repository

import com.slack.api.bolt.request.builtin.ViewSubmissionRequest
import entity.BacklogAuthInfoEntity

trait StoreRepository {
  def getBacklogAuthInfo(teamId :String, userId :String):BacklogAuthInfoEntity
  def createBacklogAuthInfo(teamId :String, userId :String, request :ViewSubmissionRequest):Unit
  def getMostRecentMessageLink(teamId :String, userId :String):String
  def createMostRecentMessageLink(teamId :String, userId :String, url: String):Unit
}
