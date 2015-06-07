import sbt.Project.projectToRef

lazy val clients = Seq(scalajsclient)
lazy val scalaV = "2.11.6"

lazy val playserver = (project in file("play")).settings(
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  pipelineStages := Seq(scalaJSProd, gzip),
  libraryDependencies ++= Seq(
    "com.vmunier" %% "play-scalajs-scripts" % "0.2.2",
    "org.webjars" %% "webjars-play" % "2.4.0",
    "org.webjars" % "bootstrap" % "3.3.4",
    "org.webjars" % "jquery" % "2.1.4",
    specs2 % Test
  ),
  routesGenerator := InjectedRoutesGenerator
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val scalajsclient = (project in file("scalajs")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  sourceMapsDirectories += sharedJs.base / "..",
  unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "com.github.japgolly.scalajs-react" %%% "core" % "0.9.0",
    "com.github.japgolly.scalajs-react" %%% "extra" % "0.9.0",
    "com.lihaoyi" %%% "scalatags" % "0.5.2"
  ),
  jsDependencies ++= Seq(
    "org.webjars" % "react" % "0.13.3" / "react-with-addons.js" commonJSName "React"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(scalaVersion := scalaV).
  jsConfigure(_ enablePlugins ScalaJSPlay).
  jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project playserver", _: State)) compose (onLoad in Global).value

