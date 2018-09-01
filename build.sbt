import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}
import sbtcrossproject.Platform

name := "arcomage-challenge"
version := "0.0.1"
scalaVersion := "2.12.6"


lazy val core = crossProject()
    .crossType(CrossType.Pure)
    .settings(compilerFlags, coreLibs, plugins)
  .settings(
    scalacOptions in Compile ~= { _.filterNot(_ contains "warn-unused") }
  )

lazy val frontend = crossProject(JSPlatform)
  .withoutSuffixFor(JSPlatform)
  .crossType(CrossType.Pure)
  .settings(compilerFlags, plugins)
  .dependsOn(core)

def compilerFlags = {
  scalacOptions ~= { _.filterNot(Set("-Xfatal-warnings")) }
}

def coreLibs = {
  libraryDependencies ++= Seq(
    "org.typelevel" %%% "cats-core" % "1.2.0",
    "org.typelevel" %%% "cats-effect" % "1.0.0-RC3",
    "io.suzaku" %%% "boopickle" % "1.3.0",
    "com.chuusai" %%% "shapeless" % "2.3.3",
    "com.github.julien-truffaut" %%% "monocle-core" % "1.5.0-cats",
    "com.github.julien-truffaut" %%% "monocle-macro" % "1.5.0-cats",
    "com.github.mpilquist" %% "simulacrum" % "0.13.0",
    "org.scalatest" %%% "scalatest" % "3.0.1" % Test
  )
}

def plugins = Seq(
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.full)
)
