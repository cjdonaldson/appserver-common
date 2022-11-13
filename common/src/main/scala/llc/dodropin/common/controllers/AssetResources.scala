package llc.dodropin.common.controllers

import llc.dodropin.common.Logging

import akka.http.scaladsl.server.Directives._

final case class AssetResources(resourceRoot: String) extends Logging {
  private val imagePath = "www/images/"

  val getResourceImage = getResourceFrom(imagePath)
  val getResoureFileImage = getResourceFile(imagePath)

  private val htmlPath = "www/html/"
  val getResourceHtml = getResourceFrom(htmlPath)
  val getResourceFileHtml = getResourceFile(htmlPath)

  private val jsPath = "www/javascript/"
  val getResourceJavascript = getResourceFrom(jsPath)

  private lazy val rootDir: Option[String] =
    Option(System.getProperty("user.dir"))
      .filterNot(_ == "/")
      .map(_ + resourceRoot)

  def getResourceFile(subdir: String) =
    rootDir
      .map { path => file: String => getFromFile(path + subdir + file) }
      .getOrElse { file: String => getFromResource(subdir + file) }

  def getResourceFrom(subdir: String) =
    rootDir
      .map(path => getFromDirectory(path + subdir))
      .getOrElse(getFromResourceDirectory(subdir))

  log.info("resource location: {}", rootDir.getOrElse("/"))
}
