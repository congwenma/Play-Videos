import models.TaskListInMemoryModel
import org.scalatestplus.play.PlaySpec

class TaskListInMemoryModelSpec extends PlaySpec {
  "TaskListInMemoryModel" must {
    "do valid login for default user" in {
      TaskListInMemoryModel.validateUser("abc", "test") mustBe (true)
    }

    "reject login with wrong password" in {
      TaskListInMemoryModel.validateUser("abc", "test123") mustBe (false)
    }

    "reject login with wrong username" in {
      TaskListInMemoryModel.validateUser("abc123", "test") mustBe (false)
    }


    "get correct default tasks" in {
      TaskListInMemoryModel.getTasks("abc") mustBe (List("Make Videos", "Eat", "Code"))
    }
  }
}