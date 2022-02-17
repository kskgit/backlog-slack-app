package config

import com.google.inject.AbstractModule
import endpoint.EndPoint
import endpoint.impl.EndPointSocketMode
import repository.BacklogRepository
import repository.impl.BacklogRepositoryImpl
import service.SlackEventHandleService
import service.impl.SlackEventHandleServiceImpl

class DIModule extends AbstractModule {
  override protected def configure(): Unit = {
    bind(classOf[SlackEventHandleService]).to(classOf[SlackEventHandleServiceImpl])
    bind(classOf[BacklogRepository]).to(classOf[BacklogRepositoryImpl])
    bind(classOf[EndPoint]).to(classOf[EndPointSocketMode])
  }
}
