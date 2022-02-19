package config

import com.google.inject.AbstractModule
import endpoint.EndPoint
import endpoint.impl.EndPointSocketMode
import repository.client.FireStoreClientInitialized
import repository.{BacklogRepository, StoreRepository}
import repository.impl.{BacklogRepositoryImpl, FireStoreRepositoryImpl}
import service.SlackEventHandleService
import service.impl.SlackEventHandleServiceImpl

class DIModuleConfig extends AbstractModule {
  override protected def configure(): Unit = {
    bind(classOf[SlackEventHandleService]).to(classOf[SlackEventHandleServiceImpl])
    bind(classOf[BacklogRepository]).to(classOf[BacklogRepositoryImpl])
    bind(classOf[EndPoint]).to(classOf[EndPointSocketMode])
    bind(classOf[FireStoreClientInitialized]).to(classOf[FireStoreClientInitialized])
    bind(classOf[StoreRepository]).to(classOf[FireStoreRepositoryImpl])
  }
}
