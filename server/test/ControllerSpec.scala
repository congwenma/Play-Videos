import org.scalatestplus.play.PlaySpec
import play.api.test.{FakeRequest, Helpers}
import controllers.Application
import play.api.test.Helpers._

// NOTE: this approach doesn't work for CSRF token, so `taskListController` is no go with this.
class ControllerSpec extends PlaySpec {
  "Application#index" must {
    "give back expected page" in {
      val controller = new Application(Helpers.stubControllerComponents())
      val result = controller.index.apply(FakeRequest())
      // Also need to import `defaultAwaitTimeout`
      val bodyText: String = contentAsString(result)
      bodyText must include("Play shouts out")
      bodyText must include("Scala.js shouts out")
    }
  }
}
