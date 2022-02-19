package repository

import entity.BacklogInfoEntity

trait StoreRepository {
  def getBacklogKey(channelId :String, userId :String):BacklogInfoEntity
  def setBacklogKey(channelId :String, userId :String, spaceId:String, apiKey:String):Unit
}
