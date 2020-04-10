//logLevel := Level.Warn
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "0.6.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.32")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.14.0")
addSbtPlugin("org.duhemm" % "sbt-errors-summary" % "0.6.0")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.3")
addSbtPlugin("com.typesafe.sbt"          % "sbt-git"              % "0.9.3")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.11")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.0")
resolvers += Resolver.bintrayRepo("oyvindberg", "ScalablyTyped")
addSbtPlugin("org.scalablytyped" % "sbt-scalablytyped" % "202004090420")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")