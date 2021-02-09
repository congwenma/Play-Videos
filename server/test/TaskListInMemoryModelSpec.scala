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

    "create new user with no tasks" in {
      TaskListInMemoryModel.createUser("newuser", "test") mustBe (true)
      TaskListInMemoryModel.getTasks("newuser") mustBe (Nil)
    }

    "create new user with existing name" in {
      TaskListInMemoryModel.createUser("abc", "testtesttest") mustBe (false)
    }

    "add new task for default user" in {
      TaskListInMemoryModel.addTask("abc", "testing")
      TaskListInMemoryModel.getTasks("abc") must contain("testing")
    }

    "remove task from default user" in {
      TaskListInMemoryModel.removeTask("abc", "Make Videos")
      TaskListInMemoryModel.getTasks("abc") must not contain "Make Videos"
    }
  }
}