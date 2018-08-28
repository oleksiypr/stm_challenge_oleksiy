lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "op.assessment",
      scalaVersion := "2.12.4",
      scalacOptions += "-Ypartial-unification"
    )),
    name := "snd-draw"
  )

libraryDependencies += "org.scalatest"  %% "scalatest"    % "3.0.5" % Test
