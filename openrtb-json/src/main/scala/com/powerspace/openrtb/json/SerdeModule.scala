package com.powerspace.openrtb.json

import com.google.openrtb.{BidRequest, BidResponse}
import com.powerspace.openrtb.json.OpenRtbExtensions.ExtensionRegistry
import com.powerspace.openrtb.json.bidrequest._
import com.powerspace.openrtb.json.bidresponse.{OpenRtbBidResponseSerde, OpenRtbBidSerde, OpenRtbSeatBidSerde}
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder}

abstract class SerdeModule {

  implicit def extensionRegistry: ExtensionRegistry

  protected val impressionLevelSerde = new ImpressionLevelSerdes()
  implicit val bannerEncoder = impressionLevelSerde.bannerEncoder
  implicit val videoEncoder = impressionLevelSerde.videoEncoder
  implicit val audioEncoder = impressionLevelSerde.audioEncoder
  implicit val dealEncoder = OpenRtbPmpSerde.dealEncoder
  implicit val pmpEncoder = impressionLevelSerde.pmpEncoder
  implicit val nativeEncoder = OpenRtbNativeRequestSerde.encoder

  implicit val bannerDecoder = impressionLevelSerde.bannerDecoder
  implicit val videoDecoder = impressionLevelSerde.videoDecoder
  implicit val audioDecoder = impressionLevelSerde.audioDecoder
  implicit val dealDecoder = OpenRtbPmpSerde.dealDecoder
  implicit val pmpDecoder = impressionLevelSerde.pmpDecoder
  implicit val nativeDecoder = OpenRtbNativeRequestSerde.decoder

  protected val userSerde = new OpenRtbUserSerde()
  implicit val userEncoder = userSerde.encoder
  implicit val userDecoder = userSerde.decoder

  protected val impressionSerde = new OpenRtbImpressionSerde()
  implicit val impEncoder = impressionSerde.encoder
  implicit val impDecoder = impressionSerde.decoder

  private val bidSerde = new OpenRtbBidSerde()
  private val seatBidSerde = new OpenRtbSeatBidSerde()

  implicit val bidEncoder = bidSerde.encoder
  implicit val seatBidEncoder = seatBidSerde.encoder
  implicit val bidDecoder = bidSerde.decoder
  implicit val seatBidDecoder = seatBidSerde.decoder

  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames

  private val bidRequestSerde = new OpenRtbBidRequestSerde()(extensionRegistry)
  implicit val bidRequestEncoder: Encoder[BidRequest] = bidRequestSerde.OpenRtbBidRequestEncoder.encoder
  implicit val bidRequestDecoder: Decoder[BidRequest] = bidRequestSerde.OpenRtbBidRequestDecoder.decoder

  private val bidResponseSerde = new OpenRtbBidResponseSerde()(extensionRegistry)
  implicit val bidResponseEncoder: Encoder[BidResponse] = bidResponseSerde.encoder
  implicit val bidResponseDecoder: Decoder[BidResponse] = bidResponseSerde.decoder

}

