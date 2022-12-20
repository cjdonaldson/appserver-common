package llc.dodropin.common.akka.controllers

import llc.dodropin.common.Logging

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

final case class AssetResources(resourceRoot: String) extends Logging {
  private val imagePath = "www/images/"

  val getResourceImage: Route = getResourceFrom(imagePath)
  val getResoureFileImage: String => Route = getResourceFile(imagePath)

  private val htmlPath = "www/html/"
  val getResourceHtml: Route = getResourceFrom(htmlPath)
  val getResourceFileHtml: String => Route = getResourceFile(htmlPath)

  private val jsPath = "www/javascript/"
  val getResourceJavascript: Route = getResourceFrom(jsPath)
  val getResourceFileJavascript: String => Route = getResourceFile(jsPath)

  private lazy val rootDir: Option[String] =
    Option(System.getProperty("user.dir"))
      .filterNot(_ == "/")
      .map(_ + resourceRoot)

  def getResourceFile(subdir: String): String => Route =
    rootDir
      .map { path => file: String => getFromFile(path + subdir + file) }
      .getOrElse { file: String => getFromResource(subdir + file) }

  def getResourceFrom(subdir: String): Route =
    rootDir
      .map(path => getFromDirectory(path + subdir))
      .getOrElse(getFromResourceDirectory(subdir))

  log.info("resource location: {}", rootDir.getOrElse("/"))
}
