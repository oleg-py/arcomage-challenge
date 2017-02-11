name := "arcomage-challenge"
version := "0.0.1"
scalaVersion := "2.12.1"

enablePlugins(ScalaJSPlugin)

libraryDependencies ++= Seq(
  "com.chuusai" %%% "shapeless" % "2.3.2",
  "com.github.julien-truffaut" %%% "monocle-core" % "1.4.0",
  "com.github.julien-truffaut" %%% "monocle-macro" % "1.4.0",
  "org.scalatest" %%% "scalatest" % "3.0.1" % Test
)

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
