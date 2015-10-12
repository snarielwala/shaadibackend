/**
 * Created by pdesai on 10/11/15.
 */

import controllers.aws.s3
import play.api._
import src.main.scala.sqlClient

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {
    s3.init()
    sqlClient.init()
    Logger.info("Application has started")
  }

  override def onStop(app: play.api.Application) {
    Logger.info("Application shutdown...")
  }

}