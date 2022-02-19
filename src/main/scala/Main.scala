import com.google.inject.{Guice, Injector}
import config.DIModuleConfig
import endpoint.EndPoint

object Main {
  def main(args: Array[String]): Unit = {
    val module = new DIModuleConfig()
    val injector: Injector = Guice.createInjector(module)
    val endPoint: EndPoint = injector.getInstance(classOf[EndPoint])
    endPoint.startServer()
  }
}