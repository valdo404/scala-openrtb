package com.powerspace.openrtb.json.bidrequest

import com.google.openrtb.BidRequest.Imp
import com.google.openrtb.BidRequest.Imp.Pmp.Deal
import com.powerspace.openrtb.json.EncoderProvider
import com.powerspace.openrtb.json.util.EncodingUtils
import com.powerspace.openrtb.json.common.OpenRtbProtobufEnumEncoders
import com.powerspace.openrtb.json.common.OpenRtbProtobufEnumDecoders

/**
  * OpenRTB Pmp Encoder and Decoder
  * @todo split up decoder and encoder
  */
object OpenRtbPmpSerde extends EncoderProvider[Imp.Pmp] {

  import EncodingUtils._
  import OpenRtbProtobufEnumEncoders._
  import OpenRtbProtobufEnumDecoders._
  import io.circe._

  val dealEncoder: Encoder[Imp.Pmp.Deal] = openRtbEncoder[Deal]

  def encoder(implicit dealEncoder: Encoder[Imp.Pmp.Deal]): Encoder[Imp.Pmp] = openRtbEncoder[Imp.Pmp]

  val dealDecoder: Decoder[Imp.Pmp.Deal] = openRtbDecoder[Deal]

  def decoder(implicit dealDecoder: Decoder[Imp.Pmp.Deal]): Decoder[Imp.Pmp] = openRtbDecoder[Imp.Pmp]

}
