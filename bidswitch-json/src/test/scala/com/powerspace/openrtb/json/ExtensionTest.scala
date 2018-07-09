package com.powerspace.openrtb.json

import com.google.openrtb.{BidRequest, BidResponse}
import com.powerspace.bidswitch.{BidRequestExt, BidswitchProto}
import com.powerspace.openrtb.bidswitch.BidSwitchSerdeModule
import com.powerspace.openrtb.bidswitch.bidrequest.BidSwitchBidRequestSerde
import com.powerspace.openrtb.bidswitch.bidresponse.BidSwitchBidResponseSerde
import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.scalatest.{FunSuite, GivenWhenThen}
import scalapb.GeneratedExtension

class ExtensionTest extends FunSuite with GivenWhenThen {

  import OpenRtbExtensions._

  val bidRequestExt: GeneratedExtension[BidRequest, Option[BidRequestExt]] = BidswitchProto.bidRequestExt
  val bidRequestExtEncoder: Encoder[BidRequestExt] = BidSwitchBidRequestSerde.bidRequestExtEncoder
  val bidRequestExtDecoder: Decoder[BidRequestExt] =  BidSwitchBidRequestSerde.bidRequestExtDecoder

  private val module = BidSwitchSerdeModule
  val baseEncoder = module.bidResponseEncoder
  val baseDecoder = module.bidResponseDecoder

  test("Automatic encoder builder") {
    val bidswitchDecoder = module.bidResponseDecoder
    val bidswitchRespEncoder = module.bidResponseEncoder

    val bidResponse = com.powerspace.openrtb.bidswitch.BidResponseFixtures.sampleBidResponse(true)

    val responseJson = bidswitchRespEncoder.apply(bidResponse)
    println(responseJson)

    val resp = bidswitchDecoder.decodeJson(responseJson)
    val bidResponseExtension = resp.map(_.extension(BidswitchProto.bidResponseExt)).toTry.get.get
    println(BidSwitchBidResponseSerde.bidResponseExtDecoder.decodeJson(responseJson))

    assert(bidResponseExtension.protocol.contains("protocol-1"))
    println(resp)
  }

}
