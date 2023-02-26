import $ivy.`com.goyeau::mill-scalafix::0.2.10`
import com.goyeau.mill.scalafix.ScalafixModule

import $ivy.`com.lihaoyi::mill-contrib-versionfile:`
import mill.contrib.versionfile.VersionFileModule

import $ivy.`com.lihaoyi::mill-contrib-scoverage:$MILL_VERSION`
import mill.contrib.scoverage.ScoverageModule

import $ivy.`com.lihaoyi::mill-contrib-docker:$MILL_VERSION`
import contrib.docker.DockerModule

import $ivy.`com.lihaoyi::mill-contrib-scalapblib:$MILL_VERSION`
import mill.contrib.scalapblib._
// extends  ScalaPBModule
import $ivy.`com.lihaoyi::mill-contrib-buildinfo:$MILL_VERSION`
import mill.contrib.buildinfo.BuildInfo

import $ivy.`com.goyeau::mill-git::0.2.3`
import com.goyeau.mill.git.GitVersionedPublishModule

import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.3.0`
import de.tobiasroeser.mill.vcs.version.VcsVersion

import mill._
import scalalib._
import scalafmt._
import publish._

trait BaseProjectModule
    extends ScalaModule
    with ScalafixModule
    with PublishModule
    with ScalafmtModule
    with ScoverageModule
    with BuildInfo
    with GitVersionedPublishModule {
  trait BaseTestModule extends Tests with TestModule.ScalaTest with ScalafmtModule with ScoverageTests {
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }

  def scoverageVersion = T { "2.0.5" }

  override def scalaVersion = "2.13.8"

  def scalacCommonOptions =
    Seq(
      "-deprecation", //                       Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", //                       Specify character encoding used by source files.
      "-explaintypes", //                      Explain type errors in more detail.
      "-feature", //                           Emit warning and location for usages of features that should be imported explicitly.
      "-Xfatal-warnings", //                   Fail the compilation if there are any warnings.
      // "-Xfuture", //                        Turn on future language features.
      "-Xlint:adapted-args", //                Warn if an argument list is modified to match the receiver.
      // "-Xlint:by-name-right-associative", //  By-name parameter of right associative operator.
      "-Xlint:constant", //                    Evaluation of a constant arithmetic expression results in an error.
      "-Xlint:delayedinit-select", //          Selecting member of DelayedInit.
      "-Xlint:doc-detached", //                A Scaladoc comment appears to be detached from its element.
      "-Xlint:inaccessible", //                Warn about inaccessible types in method signatures.
      "-Xlint:infer-any", //                   Warn when a type argument is inferred to be `Any`.
      "-Xlint:missing-interpolator", //        A string literal appears to be missing an interpolator id.
      // "-Xlint:nullary-override", //           Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Xlint:nullary-unit", //                Warn when nullary methods return Unit.
      "-Xlint:option-implicit", //             Option.apply used implicit view.
      "-Xlint:package-object-classes", //      Class or object defined in package object.
      "-Xlint:poly-implicit-overload", //      Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow", //              A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align", //                 Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow", //       A local type parameter shadows a type already in scope.
      // "-Xlint:unsound-match", //              Pattern match may not be typesafe.
      // "-Yno-adapted-args", //                 Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
      // "-Ypartial-unification", //             Enable partial unification in type constructor inference
      "-Ywarn-dead-code", //                   Warn when dead code is identified.
      "-Ywarn-extra-implicit", //              Warn when more than one implicit parameter section is defined.
      // "-Ywarn-inaccessible", //               Warn about inaccessible types in method signatures.
      // "-Ywarn-infer-any", //                  Warn when a type argument is inferred to be `Any`.
      // "-Ywarn-nullary-override", //           Warn when non-nullary `def f()' overrides nullary `def f'.
      // "-Ywarn-nullary-unit", //               Warn when nullary methods return Unit.
      "-Ywarn-numeric-widen", //               Warn when numerics are widened.
      "-Ywarn-unused:implicits", //            Warn if an implicit parameter is unused.
      "-Ywarn-unused:imports", //              Warn if an import selector is not referenced.
      "-Ywarn-unused:locals", //               Warn if a local definition is unused.
      // "-Ywarn-unused:params", //              Warn if a value parameter is unused.
      "-Ywarn-unused:patvars", //              Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates", //             Warn if a private member is unused.
      "-Ywarn-value-discard" //                Warn when non-Unit expression results are unused.
    )

  def scalacOptions = scalacCommonOptions
}

object versionFile extends VersionFileModule

// override def scalaPBSources: Sources = T.sources { millSourcePath / 'protobuf }
// override def scalaPBSources: Sources = T.sources { millOuterCtx.millSourcePath / 'protobuf }

object common extends Cross[Common]("2.12", "2.13")
class Common(val crossScalaVersion: String) extends CrossScalaModule with BaseProjectModule {
  import CommonConfig._

  def suffix = T { crossScalaVersion }
  def bigSuffix = T { suffix().toUpperCase() }

  def scalaVersion = CommonConfig.scalaVersion(crossScalaVersion)

  def publishVersion = versionFile.currentVersion().toString

  def pomSettings = CommonConfig.pomSettings("core")

  def buildInfoMembers: T[Map[String, String]] = T {
    Map[String, String](
      "name" -> "Common",
      "version" -> publishVersion(),
      "hash" -> VcsVersion.vcsState().format(),
      "scalaVersion" -> crossScalaVersion
    )
  }

  def buildInfoPackageName: Option[String] = Some("llc.dodropin.common")

  def ivyDeps =
    ivyCommonDeps ++
      ivyAkkaDeps

  def scalacOptions = scalacCommonOptions

  object test extends CrossScalaModuleTests with BaseTestModule {
    def ivyDeps = ivyTestDeps ++
      ivyAkkaTestDeps ++
      ivyLogging
  }
}

object akka extends Cross[Akka]("2.12", "2.13")
class Akka(val crossScalaVersion: String) extends CrossScalaModule with BaseProjectModule {
  import CommonConfig._

  def suffix = T { crossScalaVersion }
  def bigSuffix = T { suffix().toUpperCase() }

  def scalaVersion = CommonConfig.scalaVersion(crossScalaVersion)

  def publishVersion = versionFile.currentVersion().toString

  override def artifactName = "common-akka"

  // def moduleDeps = Seq(common)

  def pomSettings = CommonConfig.pomSettings("akka based components")

  def buildInfoMembers: T[Map[String, String]] = T {
    Map[String, String](
      "name" -> "Akka",
      "version" -> publishVersion(),
      "hash" -> VcsVersion.vcsState().format(),
      "scalaVersion" -> crossScalaVersion
    )
  }

  def buildInfoPackageName: Option[String] = Some("llc.dodropin.common.akka")

  def ivyDeps =
    ivyCommonDeps ++
      ivyAkkaDeps ++
      ivyDoDropInCommon(publishVersion())

  def scalacOptions = scalacCommonOptions

  object test extends CrossScalaModuleTests with BaseTestModule {
    def ivyDeps = ivyTestDeps ++
      ivyAkkaTestDeps ++
      ivyLogging
  }

}

object http4s extends Cross[Http4s]("2.12", "2.13")
class Http4s(val crossScalaVersion: String) extends CrossScalaModule with BaseProjectModule {
  import CommonConfig._

  def suffix = T { crossScalaVersion }
  def bigSuffix = T { suffix().toUpperCase() }

  def scalaVersion = CommonConfig.scalaVersion(crossScalaVersion)

  def publishVersion = versionFile.currentVersion().toString

  override def artifactName = "common-http4s"

  def pomSettings = CommonConfig.pomSettings("http4s based components")

  def buildInfoMembers: T[Map[String, String]] = T {
    Map[String, String](
      "name" -> "http4s",
      "version" -> publishVersion(),
      "hash" -> VcsVersion.vcsState().format(),
      "scalaVersion" -> crossScalaVersion
    )
  }

  def buildInfoPackageName: Option[String] = Some("llc.dodropin.common.http4s")

  def ivyDeps =
    ivyCommonDeps ++
      ivyHttp4sDeps ++
      ivyDoDropInCommon(publishVersion())

  def ivyHttp4sDeps = {
    def http4sVersion = "0.23.10"
    Agg(
      ivy"org.http4s::http4s-dsl:$http4sVersion",
      ivy"org.http4s::http4s-blaze-server:$http4sVersion",
      ivy"org.http4s::http4s-core:$http4sVersion",
      ivy"org.http4s::http4s-circe:$http4sVersion"
    )
  }

  def scalacOptions = scalacCommonOptions

  object test extends CrossScalaModuleTests with BaseTestModule {
    def ivyDeps = ivyTestDeps ++
      ivyLogging
  }
}

object circe extends Cross[Circe]("2.12", "2.13")
class Circe(val crossScalaVersion: String) extends CrossScalaModule with BaseProjectModule {
  import CommonConfig._

  def suffix = T { crossScalaVersion }
  def bigSuffix = T { suffix().toUpperCase() }

  def scalaVersion = CommonConfig.scalaVersion(crossScalaVersion)

  def publishVersion = versionFile.currentVersion().toString

  override def artifactName = "common-circe"

  def pomSettings = CommonConfig.pomSettings("circe")

  def buildInfoMembers: T[Map[String, String]] = T {
    Map[String, String](
      "name" -> "circe",
      "version" -> publishVersion(),
      "hash" -> VcsVersion.vcsState().format(),
      "scalaVersion" -> crossScalaVersion
    )
  }

  def buildInfoPackageName: Option[String] = Some("llc.dodropin.common.circe")

  def ivyDeps =
    ivyCommonDeps ++
      ivyAkkaDeps ++
      ivyCirceDeps ++
      ivyJwt ++
      ivyDoDropInCommon(publishVersion())

  def scalacOptions = scalacCommonOptions

  object test extends CrossScalaModuleTests with BaseTestModule {
    def ivyDeps = ivyTestDeps ++
      ivyCirceDeps ++
      ivyLogging
  }

  def ivyCirceDeps = {
    val circeVersion = "0.14.1"
    Agg(
      ivy"io.circe::circe-core:$circeVersion",
      ivy"io.circe::circe-generic:$circeVersion",
      ivy"io.circe::circe-parser:$circeVersion"
    )
  }
}

object CommonConfig {

  def pomSettings(subDescription: String) = PomSettings(
    description = s"Appserver common libraries - $subDescription",
    organization = "llc.dodropin",
    url = "https://github.com/cjdonaldson/appserver-common",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("cjdonaldson", "appserver-common"),
    developers = Seq(
      Developer("cjdonaldson", "Charles Donaldson", "https://github.com/cjdonaldson")
    )
  )

  def scalaVersion(crossScalaVersion: String) = crossScalaVersion match {
    case "2.13" => "2.13.8"
    case "2.12" => "2.12.16"
  }

  def ivyCommonDeps = {
    val guiceVersion = "5.1.0"
    Agg(
      ivy"com.typesafe:config:1.4.0",
      ivy"javax.inject:javax.inject:1",
      ivy"com.google.inject:guice:$guiceVersion",
      ivy"net.codingwell:scala-guice_2.13:$guiceVersion",
      ivy"mysql:mysql-connector-java:8.0.21", // sub for dbc,
      ivy"com.typesafe.scala-logging::scala-logging:3.9.2"
    )
  }

  private val akkaVersion = "2.6.15"
  private val akkaHttpVersion = "10.2.1"
  def ivyAkkaDeps = {
    val akkaCirceVersion = "1.29.1"
    val akkaCorsVersion = "1.1.0"
    Agg(
      ivy"com.typesafe.akka::akka-http:$akkaHttpVersion",
      ivy"com.typesafe.akka::akka-actor-typed:$akkaVersion",
      ivy"com.typesafe.akka::akka-stream:$akkaVersion",
      ivy"de.heikoseeberger::akka-http-circe:$akkaCirceVersion",
      ivy"ch.megard::akka-http-cors:$akkaCorsVersion"
    )
  }

  def ivyAkkaTestDeps =
    Agg(
      ivy"com.typesafe.akka::akka-actor-testkit-typed:$akkaVersion",
      ivy"com.typesafe.akka::akka-http-testkit:$akkaHttpVersion",
      ivy"com.typesafe.akka::akka-stream-testkit:$akkaVersion"
    )

  def ivyJwt =
    Agg(
      ivy"com.github.jwt-scala::jwt-circe:9.1.1"
    )

  def ivyTestDeps =
    Agg(
      ivy"org.scalactic::scalactic:3.2.14",
      ivy"org.scalatest::scalatest:3.2.14"
    )

  def ivyLogging = {
    // val slf4jVersion = "1.7.32"
    Agg(
      // ivy"org.slf4j:slf4j-api:$slf4jVersion",
      // ivy"org.slf4j:slf4j-core:$slf4jVersion",
      // ivy"org.slf4j:slf4j-simple:$slf4jVersion", // multiple binding like message
      ivy"ch.qos.logback:logback-classic:1.4.1" // slf4j and config file support
    )
  }

  def ivyInject =
    Agg(
      ivy"javax.inject:javax.inject:1"
    )

  def ivyDoDropInCommon(version: String) = {
    Agg(
      ivy"llc.dodropin::common:${version}"
    )
  }

}
