name := "arcomage-challenge"
version := "0.0.1"

enablePlugins(ScalaJSPlugin)

skip in packageJSDependencies := false
scalaJSUseMainModuleInitializer := true

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

// === Temporary TLS - ScalaJS fix ===

// Remove the dependency on the scalajs-compiler
libraryDependencies := libraryDependencies.value.filterNot(_.name == "scalajs-compiler")
// And add a custom one
addCompilerPlugin("org.scala-js" % "scalajs-compiler" % scalaJSVersion cross CrossVersion.patch)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.patch)
