package llc.dodropin.common.controllers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class AssetsRoutesController(resourceRoot: String) {
  val resources = AssetResources(resourceRoot)

  val routes: Route = {
    concat(
      path("favicon.ico") {
        encodeResponse {
          resources.getResoureFileImage("favicon.png")
        }
      },
      pathPrefix("html") {
        encodeResponse {
          resources.getResourceHtml
        }
      },
      pathPrefix("js") {
        encodeResponse {
          resources.getResourceJavascript
        }
      },
      pathPrefix("images") {
        encodeResponse {
          resources.getResourceImage
        }
      }
    )
  }
}
