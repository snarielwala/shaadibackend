package controllers

import java.io.File

import model.Photo
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import src.main.scala.SQLClient
import play.api.Logger


class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def newsfeed = Action { request =>
    val address=request.remoteAddress
    val timestamp = System.currentTimeMillis()

    Logger.info("Newsfeed Request from IP:"+address +",Time:"+timestamp)

    SQLClient.listPhotos match {
      case Left(e) =>{
        Logger.info("Empty Photo List, from IP:"+address + ", Time:"+timestamp)
        Ok(e)
      }
      case Right(listOfPhotos) => {
        Logger.info("Responding with Photo List, from IP:"+address + ", Time:"+timestamp)
        Ok(Json.toJson(listOfPhotos))
      }
    }
  }

  def postPhoto = Action(parse.multipartFormData) { request =>

    val address=request.remoteAddress
    val timestamp = System.currentTimeMillis()

    Logger.info("PostPhoto Request from IP:"+address +", Time:"+timestamp)

    request.body.file("file").map { photo =>
      val photoFilename = photo.filename
      val contentType = photo.contentType.get
      val localFile = new File("/tmp/" + DateTime.now() + photoFilename)
      photo.ref.moveTo(localFile)

      Logger.info("Photo moved to Location:"+localFile.toString+"from IP:"+address +", Time:"+timestamp)

      val uploadResult = aws.S3Client.uploadFile(localFile.getName, localFile)
      uploadResult match {
        case Right(url) => Logger.info("Photo Upload to s3 successful:"+uploadResult.right.get.toString+"from IP:"+address +", Time:"+timestamp)
          SQLClient.insertPhoto(new Photo(1,request.body.dataParts("caption").head.toString(), url, contentType))
          Ok("{image_uploaded}")
        case Left(message) => Logger.info("Photo Upload to s3 failed:"+message+"from IP:"+address+", Time:"+timestamp)
          Ok("Upload failed. Try again.")
      }
    }.getOrElse {
      Logger.info("Invalid Parameters from IP:"+address+", Time:"+timestamp)
      Ok("Invalid parameters.")
    }
  }

  def debug = Action {
    Ok("<!doctype html>\n   <title>Upload new File</title>\n   <h1>Upload new File</h1>\n   <form action=\"http://localhost:9000/photos\" method=post enctype=multipart/form-data>\n     <p><input type=file name=file>\n        <input type=submit value=Upload>\n   </form>").as("text/html")
  }

}
