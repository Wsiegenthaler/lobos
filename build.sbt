
scalaVersion in ThisBuild := "2.13.1"

lazy val lobos = crossProject(JSPlatform, JVMPlatform).
  in(file(".")).
  settings(
    name := "lobos",
    organization := "com.github.wsiegenthaler",
    version := "0.10.0",
    crossScalaVersions := Seq("2.13.1", "2.12.10", "2.11.11"),
    resolvers ++= Seq(
     "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
     "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
    ),
    libraryDependencies  ++= Seq()
  ).
  jvmSettings(
    unmanagedResourceDirectories in Compile += baseDirectory.value / ".." / "shared" / "src" / "main" / "resources",
    libraryDependencies  ++= Seq(
      "org.scalanlp" %% "breeze" % "1.0",
      "org.scalanlp" %% "breeze-natives" % "1.0",
      "org.scalanlp" %% "breeze-viz" % "1.0"
    ),
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org"
      if (isSnapshot.value) Some("snapshots" at nexus + "/content/repositories/snapshots")
      else Some("releases" at nexus + "/service/local/staging/deploy/maven2")
    },
    useGpg := false,
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
    publish := {},
    libraryDependencies  ++= Seq(),
    scalaJSLinkerConfig ~= { _.withESFeatures(_.withUseECMAScript2015(true)) },
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
  )

lazy val lobosJVM = lobos.jvm
lazy val lobosJS = lobos.js
