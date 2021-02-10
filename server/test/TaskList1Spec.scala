import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerSuite, PlaySpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

// TODO: to run this
//  sbt> testOnly *TaskList1Spec*
// TODO: doesn't work, runs into `AbstractMethodError`, see comments on https://www.youtube.com/watch?v=TgcLu7hpElI&list=PLLMXbkbDbVt8tBiGc1y69BZdG8at1D7ZF&index=22
// Test with CSRF Token
// Single server for this entire test `GuiceOneServerPerSuite`
//  `HtmlUnitFactory` - actually create virtual browser
class TaskList1Spec
    extends PlaySpec
    with GuiceOneServerPerSuite
    with OneBrowserPerSuite
    with HtmlUnitFactory {
  "Task list 1" must {
    "can login and access functions" in {
      // NOTE: this test framework pick its own port, provided by the test framework
      go to s"http://localhost:$port/login"
//      click on ("form[action=validatePost] input[name=username]")
//      textField("form[action=validatePost] input[name=username]").value = "abc"
      click on "username-login"
      textField("username-login").value = "abc"

//      click on "form[action=validatePost] input[name=password]"
//      textField("form[action=validatePost] input[name=password]").value = "test"
      click on ("password-login")
      textField("password-login").value = "test"

      submit()
    }
  }
}
