import spray.revolver.RevolverPlugin.Revolver

name := "spray-meetup"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaVersion = "2.3.9"
  val sprayVersion = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayVersion,
    "io.spray"            %%  "spray-routing" % sprayVersion,
    "io.spray"            %%  "spray-json"    % "1.3.2",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaVersion
  )
}

libraryDependencies ++= {
  val sparkVersion = "1.5.1"
  val sparkCsvVersion = "1.2.0"
  val configVersion = "1.3.0"
  Seq(
    "org.apache.spark"  %%  "spark-core"    % sparkVersion,
    "org.apache.spark"  %%  "spark-sql"     % sparkVersion,
    "org.apache.spark"  %%  "spark-graphx"  % sparkVersion,
    "com.databricks"    %%  "spark-csv"     % sparkCsvVersion,
    "com.typesafe"      %   "config"        % configVersion
  )
}

dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value

Revolver.settings
