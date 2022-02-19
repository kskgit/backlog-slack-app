package repository

import entity.BacklogAuthInfoEntity

trait StoreRepository {
  def getBacklogAuthInfo(channelId :String, userId :String):BacklogAuthInfoEntity
  def setBacklogAuthInfo(channelId :String, userId :String, spaceId:String, apiKey:String):Unit
}
