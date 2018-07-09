package com.powerspace.openrtb.bidswitch.bidresponse

import com.google.openrtb.NativeResponse
import com.powerspace.bidswitch.{BidswitchProto, NativeResponseExt}
import com.powerspace.openrtb.json.bidresponse.OpenRtbNativeSerde
import com.powerspace.openrtb.json.util.EncodingUtils
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder, Json}

object BidSwitchNativeResponseSerde {

  import EncodingUtils._
  import io.circe.syntax._
  import io.circe.generic.extras.semiauto._

  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val nativeExtEncoder: Encoder[NativeResponseExt] = deriveEncoder[NativeResponseExt].cleanRtb

  implicit def encoder: Encoder[NativeResponse] =
    native => {
      val json: Json = native.extension(BidswitchProto.responseNativeExt).asJson
      OpenRtbNativeSerde.nativeResponseEncoder.apply(native).asObject
        .map(_.add("ext", json)).asJson
    }

  implicit val nativeExtDecoder: Decoder[NativeResponseExt] = {
    cursor =>
      val ext = cursor.downField("ext")
      for {
        viewtracker <- ext.downField("viewtracker").as[Option[String]]
        adchoiceurl <- ext.downField("adchoiceurl").as[Option[String]]
      } yield NativeResponseExt(viewtracker = viewtracker, adchoiceurl = adchoiceurl)
  }

  implicit def decoder: Decoder[NativeResponse] = for {
    native <- OpenRtbNativeSerde.decoder
    ext <- nativeExtDecoder
  } yield native.withExtension(BidswitchProto.responseNativeExt)(Some(ext))

}
