package com.powerspace.openrtb.bidswitch.bidrequest

import com.google.openrtb.BidRequest.Imp
import com.powerspace.bidswitch.{BannerExt, BidswitchProto, Format}
import com.powerspace.openrtb.bidswitch.util.JsonUtils
import com.powerspace.openrtb.json.EncoderProvider
import com.powerspace.openrtb.json.bidrequest.OpenRtbBannerSerde.OpenRtbBannerEncoder
import com.powerspace.openrtb.json.util.EncodingUtils

/**
  * Banner BidSwitch extension encoders
  */
object BidSwitchBannerSerde extends EncoderProvider[Imp.Banner] {

  import JsonUtils._
  import io.circe._
  import io.circe.syntax._
  import EncodingUtils._
  import io.circe.generic.extras.semiauto._

  implicit val formatEncoder: Encoder[Format] = deriveEncoder[Format].cleanRtb

  implicit val bannerExtEncoder: Encoder[BannerExt] = deriveEncoder[BannerExt].cleanRtb

  implicit val encoder: Encoder[Imp.Banner] = banner =>
    OpenRtbBannerEncoder.encoder.apply(banner).addExtension(banner.extension(BidswitchProto.bannerExt).asJson)

  implicit val formatDecoder: Decoder[Format] = deriveDecoder[Format]

  val bannerExtDecoder: Decoder[BannerExt] = deriveDecoder[BannerExt]

}
