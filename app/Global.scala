/**
 * Created by pdesai on 10/11/15.
 */

import controllers.aws.S3Client
import play.api._
import src.main.scala.SQLClient

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    S3Client.init()
    SQLClient.init()
    Logger.info("Application has started")
  }

  override def onStop(app: play.api.Application) {
    Logger.info("Application shutdown...")
  }

}