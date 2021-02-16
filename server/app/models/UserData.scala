package models

import play.api.libs.json.Json

case class UserData(username: String, password: String)
case class TaskItem(text: String)
