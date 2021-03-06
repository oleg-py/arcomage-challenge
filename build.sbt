import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
import sbtcrossproject.Platform
import java.lang.Runtime.getRuntime

name := "arcomage-challenge"
onLoad in Global := (onLoad in Global).value
  .andThen(Command.process("project frontend", _))

inThisBuild(Seq(
  version := "0.0.1",
  scalaVersion := "2.13.1",
))

val SinglePlatform: CrossType = new CrossType {
  def projectDir(crossBase: File, projectType: String) = crossBase
  def projectDir(crossBase: File, platform: Platform) = crossBase
  def sharedSrcDir(projectBase: File, conf: String) =
    CrossType.Pure.sharedSrcDir(projectBase, conf)
}


lazy val core = crossProject()
  .crossType(CrossType.Pure)
  .settings(compilerFlags, coreLibs, plugins)
  .settings(
    scalacOptions in Compile ~= { _.filterNot(_ contains "warn-unused") }
  )

lazy val frontend = crossProject(JSPlatform)
  .withoutSuffixFor(JSPlatform)
  .crossType(SinglePlatform)
  .settings(compilerFlags, plugins)
  .dependsOn(core)
  .enablePlugins(ScalaJSBundlerPlugin)
  .jsSettings(
    addCommandAlias("wp", ";compile;fastOptJS::startWebpackDevServer;~fastOptJS"),
    addCommandAlias("nowp", "fastOptJS::stopWebpackDevServer"),
    npmDependencies in Compile ++= Seq(
      "antd"               -> "4.1.1",
      "@ant-design/icons"  -> "4.0.5",
      "react"              -> "16.8.6",
      "react-dom"          -> "16.8.6",
      "react-proxy"        -> "1.1.8",
      "peerjs"             -> "0.3.16",
      "gravatar"           -> "1.8.0",
      "clipboard"          -> "2.0.4",
    ),

    npmDevDependencies in Compile ++= Seq(
      "file-loader"         -> "1.1.11",
      "style-loader"        -> "0.20.3",
      "css-loader"          -> "0.28.11",
      "stylus-loader"       -> "3.0.2",
      "csv-loader"          -> "3.0.2",
      "html-webpack-plugin" -> "3.2.0",
      "copy-webpack-plugin" -> "4.5.1",
      "webpack-merge"       -> "4.1.2",
      "stylus"              -> "0.54.5",
      "papaparse"           -> "4.6.0"
    ),

    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.bintrayRepo("oyvindberg", "ScalablyTyped"),

    libraryDependencies ++= Seq(
      ScalablyTyped.A.antd intransitive(),
      ScalablyTyped.G.`gravatar`,
      ScalablyTyped.C.`clipboard`,
      ScalablyTyped.R.`react-slinky-facade`,
      "me.shadaj" %%% "slinky-web" % "0.6.4",
      "me.shadaj" %%% "slinky-hot" % "0.6.4",
      "co.fs2" %%% "fs2-core" % "2.3.0",
      "io.suzaku" %%% "boopickle" % "1.3.1",
      "com.olegpy" %%% "shironeko-slinky" % "0.1.0-RC4",
      "com.olegpy" %%% "shironeko-core" % "0.1.0-RC4",
      "io.circe" %%% "circe-core" % "0.13.0",
      "io.circe" %%% "circe-generic" % "0.13.0",
      "io.circe" %%% "circe-parser" % "0.13.0",
      "io.circe" %%% "circe-refined" % "0.13.0",
    ),

    scalacOptions += "-P:scalajs:sjsDefinedByDefault",

    version in webpack := "4.5.0",
    version in startWebpackDevServer:= "3.1.3",

    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack-fastopt.config.js"),
    webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack-opt.config.js"),
    webpackConfigFile in Test := Some(baseDirectory.value / "webpack-core.config.js"),

    webpackDevServerExtraArgs in fastOptJS := Seq("--inline", "--hot"),
    webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),

    cleanFiles := {
      def recExclude(names: List[String], file: File): Seq[File] = names match {
        case Nil => Seq()
        case n :: ns => file.glob("*").flatMap {
          case f if f.name startsWith n =>
            recExclude(ns, f)
          case f => Seq(f)
        }.get
      }
      recExclude(List("scala-", "scalajs-bundler", "main", "node_modules"), target.value)
    },

  )

lazy val console = crossProject(JVMPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(SinglePlatform)
  .settings(compilerFlags, plugins)
  .dependsOn(core)
  .jvmSettings(
    libraryDependencies += "dev.profunktor" %% "console4cats" % "0.8.1"
  )

def compilerFlags = {
  scalacOptions ~= { _.filterNot(Set("-Xfatal-warnings")) ++ Seq(
    "-Ybackend-parallelism",
    Math.max(1, getRuntime.availableProcessors() - 1).toString,
    "-Ymacro-annotations",
  )}
}

def coreLibs = {
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core" % "2.1.0",
    "org.typelevel" %%% "cats-effect" % "2.0.0",
    "io.monix" %%% "monix-eval" % "3.1.0",
    "org.typelevel" %%% "kittens" % "2.0.0",
    "io.higherkindness" %%% "droste-core" % "0.8.0",
    "com.chuusai" %%% "shapeless" % "2.3.3",
    "com.github.julien-truffaut" %%% "monocle-core" % "2.0.0",
    "com.github.julien-truffaut" %%% "monocle-macro" % "2.0.0",
    "com.github.mpilquist" %%% "simulacrum" % "0.19.0",
    "eu.timepit" %%% "refined"            % "0.9.10",
    "eu.timepit" %%% "refined-cats"       % "0.9.10",
    "org.typelevel" %%% "mouse" % "0.23",
    "org.scalatest" %%% "scalatest" % "3.1.0" % Test
  )
}

def plugins = Seq(
  libraryDependencies += "com.github.ghik" %%"silencer-lib" % "1.6.0" % Provided cross CrossVersion.full,
  addCompilerPlugin("com.github.ghik" % "silencer-plugin" % "1.6.0" cross CrossVersion.full),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
)
