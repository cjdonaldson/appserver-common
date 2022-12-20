package llc.dodropin.common.http4s.controllers

import llc.dodropin.common.Logging

import cats.effect.IO
import fs2.io.file.Path
import org.http4s.{Request, Response, StaticFile}

final case class AssetResources(resourceRoot: String) extends Logging {
  private val imagePath = "www/images/"

  val getResourceImage: Request[IO] => IO[Response[IO]] = getResourceFrom(imagePath)
  val getResourceFileImage: String => Request[IO] => IO[Response[IO]] = getResourceFile(imagePath)

  private val htmlPath = "www/html/"
  val getResourceHtml: Request[IO] => IO[Response[IO]] = getResourceFrom(htmlPath)
  val getResourceFileHtml: String => Request[IO] => IO[Response[IO]] = getResourceFile(htmlPath)

  private val jsPath = "www/javascript/"
  val getResourceJavascript: Request[IO] => IO[Response[IO]] = getResourceFrom(jsPath)
  val getResourceFileJavascript: String => Request[IO] => IO[Response[IO]] = getResourceFile(jsPath)

  private lazy val rootDir: Option[String] =
    Option(System.getProperty("user.dir"))
      .filterNot(_ == "/")
      .map(_ + resourceRoot)

  def getResourceFile(subdir: String): String => Request[IO] => IO[Response[IO]] = (file: String) =>
    (request: Request[IO]) =>
      rootDir
        .map { path => StaticFile.fromPath(Path(path + subdir + file), Some(request)) }
        .getOrElse { StaticFile.fromResource(subdir + file, Some(request)) }
        .getOrElse(Response.notFound)

  def getResourceFrom(subdir: String): Request[IO] => IO[Response[IO]] =
    (request: Request[IO]) =>
      StaticFile
        .fromPath(
          rootDir
            .map(path => Path(path + subdir))
            .getOrElse(Path(subdir)),
          Some(request)
        )
        .getOrElse(Response.notFound)

  log.info("resource location: {}", rootDir.getOrElse("/"))
}
