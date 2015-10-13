package controllers

import java.io.File

import model.Photo
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import src.main.scala.SQLClient

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def newfeed = Action {
    SQLClient.listPhotos match {
      case Left(e) => Ok(e)
      case Right(listOfPhotos) => Ok(Json.toJson(listOfPhotos))
    }
  }

  def postPhoto(caption: String) = Action(parse.multipartFormData) { request =>

    request.body.file("file").map { photo =>
      val photoFilename = photo.filename
      val contentType = photo.contentType.get
      val localFile = new File("/tmp/" + photoFilename + DateTime.now())
      photo.ref.moveTo(localFile)

      println(localFile)

      val uploadResult = aws.S3Client.uploadFile(localFile.getName, localFile)
      uploadResult match {
        case Right(url) => println(uploadResult.right.get.toString)
          SQLClient.insertPhoto(new Photo(1, caption, url, contentType))
          Ok("{file_uploaded}")
        case Left(message) => println(message)
          Ok("Upload failed. Try again.")
      }
    }.getOrElse {
      Ok("Invalid parameters.")
    }
  }

  def debug = Action {
    Ok("<!doctype html>\n   <title>Upload new File</title>\n   <h1>Upload new File</h1>\n   <form action=\"http://localhost:9000/photos\" method=post enctype=multipart/form-data>\n     <p><input type=file name=file>\n        <input type=submit value=Upload>\n   </form>").as("text/html")
  }

}
