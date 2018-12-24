import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
import sbtcrossproject.Platform
import java.lang.Runtime.getRuntime

name := "arcomage-challenge"

inThisBuild(Seq(
  version := "0.0.1",
  scalaVersion := "2.12.7",
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
      "react"              -> "16.4.2",
      "react-dom"          -> "16.4.2",
      "react-proxy"        -> "1.1.8",
      "peerjs"             -> "0.3.16",
      "gravatar-url"       -> "2.0.0",
      "clipboard"          -> "2.0.4",
      "react-spinners"     -> "0.4.7",
      "rc-tabs"            -> "9.5.7",
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

    libraryDependencies ++= Seq(
      "me.shadaj" %%% "slinky-web" % "0.5.1",
      "me.shadaj" %%% "slinky-hot" % "0.5.1",
      "co.fs2" %%% "fs2-core" % "1.0.0-RC1",
      "io.suzaku" %%% "boopickle" % "1.3.0",
      "com.lihaoyi" %%% "upickle" % "0.7.1",
      "com.olegpy" %%% "shironeko-slinky" % "0.0.8"
    ),

    scalacOptions += "-P:scalajs:sjsDefinedByDefault",

    version in webpack := "4.5.0",
    version in startWebpackDevServer:= "3.1.3",

    webpackConfigFile in fastOptJS := Some(baseDirectory.value / "webpack-fastopt.config.js"),
    webpackConfigFile in fullOptJS := Some(baseDirectory.value / "webpack-opt.config.js"),
    webpackConfigFile in Test := Some(baseDirectory.value / "webpack-core.config.js"),

    webpackDevServerExtraArgs in fastOptJS := Seq("--inline", "--hot"),
    webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
  )

lazy val console = crossProject(JVMPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(SinglePlatform)
  .settings(compilerFlags, plugins)
  .dependsOn(core)
  .jvmSettings(
    libraryDependencies += "com.github.gvolpe" %% "console4cats" % "0.2"
  )

def compilerFlags = {
  scalacOptions ~= { _.filterNot(Set("-Xfatal-warnings")) ++ Seq(
    "-Ybackend-parallelism",
    Math.max(1, getRuntime.availableProcessors() - 1).toString
  )}
}

def coreLibs = {
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core" % "1.4.0",
    "org.typelevel" %%% "cats-effect" % "1.0.0",
    "io.monix" %%% "monix-eval" % "3.0.0-RC2",
    "org.typelevel" %%% "kittens" % "1.2.0",
    "io.higherkindness" %%% "droste-core" % "0.4.0",
    "com.chuusai" %%% "shapeless" % "2.3.3",
    "com.github.julien-truffaut" %%% "monocle-core" % "1.5.0-cats",
    "com.github.julien-truffaut" %%% "monocle-macro" % "1.5.0-cats",
    "com.github.mpilquist" %%% "simulacrum" % "0.13.0",
    "eu.timepit" %%% "refined"            % "0.9.2",
    "eu.timepit" %%% "refined-cats"       % "0.9.2",
    "org.typelevel" %%% "mouse" % "0.19",
    "org.scalatest" %%% "scalatest" % "3.0.1" % Test
  )
}

def plugins = Seq(
  libraryDependencies += "com.github.ghik" %% "silencer-lib" % "1.2" % Provided,
  addCompilerPlugin("com.github.ghik" %% "silencer-plugin" % "1.2"),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full),
)
