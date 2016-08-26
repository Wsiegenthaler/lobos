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
    organization := "com.github.wsiegenthaler",
    version := "0.9.23",
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
    ),
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org"
      if (isSnapshot.value) Some("snapshots" at nexus + "/content/repositories/snapshots")
      else Some("releases" at nexus + "/service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    licenses := Seq("BSD-style" -> url("https://opensource.org/licenses/BSD-3-Clause")),
    homepage := Some(url("http://github.com/wsiegenthaler/lobos")),
    pomExtra := (
      <scm>
        <connection>https://github.com/wsiegenthaler/lobos.git</connection>
        <developerConnection>git@github.com:wsiegenthaler/lobos.git</developerConnection>
        <url>https://github.com/wsiegenthaler/lobos</url>
      </scm>
      <developers>
        <developer>
          <id>wsiegenthaler</id>
          <name>Weston Siegenthaler</name>
          <url>http://github.com/wsiegenthaler</url>
        </developer>
      </developers>)
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
