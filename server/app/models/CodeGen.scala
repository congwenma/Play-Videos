package models

// NOTE: If I decide to change my database table, and have to rerun this. Should be same url as ./application.conf
//  sbt> runMain models.CodeGen
// Generates `table.scala`, and generates `class User, class Password`
object CodeGen extends App {
  slick.codegen.SourceCodeGenerator.run(
    "slick.jdbc.PostgresProfile",
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/tasklist?user=playvideos&password=password",
    "/Users/congwen.ma/dev/play-videos/server/app/",
    "models",
    None,
    None,
    true,
    false
  )
}
