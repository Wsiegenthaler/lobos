import org.scalajs.core.tools.javascript.OutputMode._

scalaVersion in ThisBuild := "2.11.8"

lazy val root = project.in(file(".")).
  aggregate(lobosJS, lobosJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val lobos = crossProject.in(file(".")).
  settings(
    name := "lobos",
    version := "0.9",
    scalaVersion := "2.11.8",
    resolvers ++= Seq(
     "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
     "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
    ),
    libraryDependencies  ++= Seq()
  ).
  jvmSettings(
    unmanagedResourceDirectories in Compile += baseDirectory.value / ".." / "shared" / "src" / "main" / "resources",
     libraryDependencies  ++= Seq(
      "org.scalanlp" %% "breeze" % "0.12",
      "org.scalanlp" %% "breeze-natives" % "0.12",
      "org.scalanlp" %% "breeze-viz" % "0.12"
    )
  ).
  jsSettings(
    libraryDependencies  ++= Seq(),
    scalaJSOutputMode := ECMAScript6,
    skip in packageJSDependencies := false, // generate dependency file (lobos-jsdeps.js)
    scalaJSOutputWrapper := // bridges gap between scalajs output and commonjs needed by webpack
      ("var g = global || window || {}, __ScalaJSEnv = { global: g, exportsNamespace: {} }; g.LobosParams = require('./lobos-params.js'); (function() {",
       "}).call(module.exports); module.exports = __ScalaJSEnv.exportsNamespace.lobos;")
  )

lazy val lobosJVM = lobos.jvm
lazy val lobosJS = lobos.js
