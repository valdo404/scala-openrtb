package com.powerspace.openrtb.json.bidrequest

import com.google.openrtb.BidRequest
import com.google.openrtb.BidRequest.{DistributionchannelOneof, Imp, Source}
import com.powerspace.openrtb.json.EncoderProvider
import com.powerspace.openrtb.json.OpenRtbExtensions.ExtensionRegistry
import com.powerspace.openrtb.json.util.EncodingUtils
import com.powerspace.openrtb.json.common.OpenRtbProtobufEnumEncoders
import com.powerspace.openrtb.json.common.OpenRtbProtobufEnumDecoders

/**
  * OpenRTB BidRequest Encoder and Decoder
  */
class OpenRtbBidRequestSerde(implicit er: ExtensionRegistry) extends EncoderProvider[BidRequest] {


  import io.circe._
  import io.circe.generic.extras.semiauto._
  import EncodingUtils._
  import com.google.openrtb._

  object OpenRtbBidRequestEncoder {

    import io.circe.syntax._
    import OpenRtbProtobufEnumEncoders._

    implicit val metricEncoder: Encoder[Imp.Metric] = new OpenRtbImpressionSerde().metricEncoder

    implicit val producerEncoder: Encoder[BidRequest.Producer] = extendedEncoder[BidRequest.Producer]
    implicit val publisherEncoder: Encoder[BidRequest.Publisher] = extendedEncoder[BidRequest.Publisher]
    implicit val contentEncoder: Encoder[BidRequest.Content] = extendedEncoder[BidRequest.Content]

    implicit val sourceEncoder: Encoder[Source] = extendedEncoder[Source]
    implicit val deviceEncoder: Encoder[BidRequest.Device] = extendedEncoder[BidRequest.Device]
    implicit val regsEncoder: Encoder[BidRequest.Regs] = extendedEncoder[BidRequest.Regs]

    implicit val distChannelOneOfEncoder: Encoder[BidRequest.DistributionchannelOneof] = protobufOneofEncoder[BidRequest.DistributionchannelOneof] {
      case BidRequest.DistributionchannelOneof.App(app) => app.asJson
      case BidRequest.DistributionchannelOneof.Site(site) => site.asJson
    }

    implicit val appEncoder: Encoder[BidRequest.App] = extendedEncoder[BidRequest.App]
    implicit val siteEncoder: Encoder[BidRequest.Site] = extendedEncoder[BidRequest.Site]
    implicit val geoEncoder: Encoder[BidRequest.Geo] = extendedEncoder[BidRequest.Geo]

    def encoder(implicit userEncoder: Encoder[BidRequest.User], impEncoder: Encoder[BidRequest.Imp]): Encoder[BidRequest] =
      extendedEncoder[BidRequest].clean(toKeep = Seq("imp"))

  }

  object OpenRtbBidRequestDecoder {

    import OpenRtbProtobufEnumDecoders._

    private val impressionSerde = new OpenRtbImpressionSerde()
    private implicit val metricDecoder: Decoder[Imp.Metric] = impressionSerde.metricDecoder

    implicit val producerDecoder: Decoder[BidRequest.Producer] = openRtbDecoder[BidRequest.Producer]
    implicit val publisherDecoder: Decoder[BidRequest.Publisher] = openRtbDecoder[BidRequest.Publisher]
    implicit val contentDecoder: Decoder[BidRequest.Content] = openRtbDecoder[BidRequest.Content]

    implicit val appDecoder: Decoder[BidRequest.App] = openRtbDecoder[BidRequest.App]
    implicit val siteDecoder: Decoder[BidRequest.Site] = openRtbDecoder[BidRequest.Site]
    implicit val geoDecoder: Decoder[BidRequest.Geo] = openRtbDecoder[BidRequest.Geo]

    implicit val channelSiteDecoder: Decoder[DistributionchannelOneof.Site] = openRtbDecoder[DistributionchannelOneof.Site]
    implicit val channelAppDecoder: Decoder[DistributionchannelOneof.App] = openRtbDecoder[DistributionchannelOneof.App]

    implicit val sourceDecoder: Decoder[BidRequest.Source] = openRtbDecoder[BidRequest.Source]
    implicit val deviceDecoder: Decoder[BidRequest.Device] = openRtbDecoder[BidRequest.Device]
    implicit val regsDecoder: Decoder[BidRequest.Regs] = openRtbDecoder[BidRequest.Regs]

    implicit val oneOf: Decoder[DistributionchannelOneof] = (c: HCursor) => Right(DistributionchannelOneof.Empty)

    def decoder(implicit userDecoder: Decoder[BidRequest.User], impDecoder: Decoder[BidRequest.Imp]): Decoder[BidRequest] = {
      for {
        bidRequest <- openRtbDecoder[BidRequest]
        app <- Decoder[Option[BidRequest.App]].prepare(_.downField("app"))
        site <- Decoder[Option[BidRequest.Site]].prepare(_.downField("site"))
        oneof = site
          .map(DistributionchannelOneof.Site)
          .orElse(app.map(DistributionchannelOneof.App))
          .getOrElse(DistributionchannelOneof.Empty)
      } yield bidRequest.copy(distributionchannelOneof = oneof)
    }

  }

}
