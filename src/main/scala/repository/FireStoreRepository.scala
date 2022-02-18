package repository

import entity.BacklogInfo

trait FireStoreRepository {
  def getBacklogKey(userId :String):BacklogInfo
  def setBacklogKey(userId :String, spaceId:String, apiKey:String):Unit
}
