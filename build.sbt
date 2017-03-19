name := "arcomage-challenge"
version := "0.0.1"
scalaVersion := "2.12.1"

scalaOrganization := "org.typelevel"

enablePlugins(ScalaJSPlugin)

skip in packageJSDependencies := false
persistLauncher := true

libraryDependencies ++= Seq(
  "org.typelevel" %%% "cats" % "0.9.0",
  "in.nvilla" %%% "monadic-html" % "latest.integration",
  "io.monix" %%% "monix" % "2.2.1",
  "io.suzaku" %%% "boopickle" % "1.2.6",
  "com.chuusai" %%% "shapeless" % "2.3.2",
  "com.github.julien-truffaut" %%% "monocle-core" % "1.4.0",
  "com.github.julien-truffaut" %%% "monocle-macro" % "1.4.0",
  "org.scalatest" %%% "scalatest" % "3.0.1" % Test
)

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
