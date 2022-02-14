import com.google.inject.{Guice, Injector}
import config.DIModule

object Main {
  def main(args: Array[String]): Unit = {
    val module = new DIModule()
    val injector: Injector = Guice.createInjector(module)
    val endPoint: EndPoint = injector.getInstance(classOf[EndPoint])
    endPoint.startSocketModeApp()
  }
}