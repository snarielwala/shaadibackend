package model

import play.api.libs.json._

/**
 * Created by pdesai on 10/11/15.
 */

case class Photo(_id: Int, caption: String, url: String, mediaType: String)

object Photo {

  implicit object TweetFormat extends Format[Photo] {

    // convert from Tweet object to JSON (serializing to JSON)
    def writes(photo: Photo): JsValue = {
      val photoSeq = Seq(
        "_id" -> JsNumber(photo._id),
        "caption" -> JsString(photo.caption),
        "mediaType" -> JsString(photo.mediaType),
        "url" -> JsString(photo.url)
      )
      JsObject(photoSeq)
    }

    // convert from JSON string to a Tweet object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[Photo] = {
      JsSuccess(Photo(1, "", "", ""))
    }
  }
}
