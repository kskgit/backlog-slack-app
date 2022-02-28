package config

import com.google.inject.AbstractModule
import endpoint.EndPoint
import endpoint.impl.EndPointImpl
import repository.impl.{BacklogRepositoryImpl, FireStoreRepositoryImpl}
import repository.{BacklogRepository, StoreRepository}
import service.SlackEventHandleService
import service.impl.SlackEventHandleServiceImpl

/** DIの対応関係を登録する */
class DIModuleConfig extends AbstractModule {
  override protected def configure(): Unit = {
    bind(classOf[EndPoint]).to(classOf[EndPointImpl])
    bind(classOf[SlackEventHandleService]).to(
      classOf[SlackEventHandleServiceImpl]
    )
    bind(classOf[BacklogRepository]).to(classOf[BacklogRepositoryImpl])
    bind(classOf[StoreRepository]).to(classOf[FireStoreRepositoryImpl])
  }
}
