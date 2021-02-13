import sbtcrossproject.{crossProject, CrossType}

lazy val server = (project in file("server")).settings(commonSettings).settings(
	name := "Play-Videos-Server",
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.vmunier" %% "scalajs-scripts" % "1.1.2",
    guice,
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
    specs2 % Test,

    //    `Slick` - Database binding for connecting to JDBC
    "com.typesafe.play" %% "play-slick" % "5.0.0",
    //
    "com.typesafe.slick" %% "slick-codegen" % "3.3.2",
    //
    "com.typesafe.play" %% "play-json" % "2.8.1",
    // database driver
    "org.postgresql" % "postgresql" % "42.2.11",
    // Manager for database connection, not sure if we need it, new slick version have it in dependency, w/e
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
    // Password hash
    "org.mindrot" % "jbcrypt" % "0.4"
  ),
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
  EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(commonSettings).settings(
	name := "Play-Videos-Client",
  scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.5",
		"org.querki" %%% "jquery-facade" % "1.2",
		"com.typesafe.play" %%% "play-json" % "2.7.0"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(
		name := "Play-Videos-Shared",
		commonSettings,
		libraryDependencies ++= Seq(
			"com.typesafe.play" %%% "play-json" % "2.7.0"
		))
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "edu.trinity"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
