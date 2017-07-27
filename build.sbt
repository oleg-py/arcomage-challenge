name := "arcomage-challenge"
version := "0.0.1"

enablePlugins(ScalaJSPlugin)

skip in packageJSDependencies := false
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "org.typelevel" %%% "cats" % "0.9.0",
  "io.monix" %%% "monix" % "2.3.0",
  "io.monix" %%% "monix-cats" % "2.3.0",
  "io.suzaku" %%% "boopickle" % "1.2.6",
  "com.chuusai" %%% "shapeless" % "2.3.2",
  "com.github.julien-truffaut" %%% "monocle-core" % "1.4.0",
  "com.github.julien-truffaut" %%% "monocle-macro" % "1.4.0",

  "com.github.japgolly.scalajs-react" %%% "core" % "1.0.1",
  "com.github.japgolly.scalajs-react" %%% "extra" % "1.0.1",

  "org.scalatest" %%% "scalatest" % "3.0.1" % Test
)

jsDependencies ++= Seq(
  "org.webjars.bower" % "react" % "15.5.4"
    /        "react-with-addons.js"
    minified "react-with-addons.min.js"
    commonJSName "React",

  "org.webjars.bower" % "react" % "15.5.4"
    /         "react-dom.js"
    minified  "react-dom.min.js"
    dependsOn "react-with-addons.js"
    commonJSName "ReactDOM",

  "org.webjars.bower" % "react" % "15.5.4"
    /         "react-dom-server.js"
    minified  "react-dom-server.min.js"
    dependsOn "react-dom.js"
    commonJSName "ReactDOMServer",

  "org.webjars.bower" % "peerjs" % "0.3.14"
    /        "peer.js"
    minified "peer.min.js"
)

// === Temporary TLS - ScalaJS fix ===

// Remove the dependency on the scalajs-compiler
libraryDependencies := libraryDependencies.value.filterNot(_.name == "scalajs-compiler")
// And add a custom one
addCompilerPlugin("org.scala-js" % "scalajs-compiler" % scalaJSVersion cross CrossVersion.patch)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.patch)
