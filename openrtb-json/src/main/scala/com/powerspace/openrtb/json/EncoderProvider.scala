package com.powerspace.openrtb.json

import com.google.openrtb.NativeRequest
import io.circe.Decoder.Result
import scalapb.GeneratedMessage
import io.circe.generic.extras.Configuration
import scalapb.{ExtendableMessage, GeneratedExtension}
import io.circe._

import scala.reflect.ClassTag

/**
  * The encoder provider is an encoder builder
  *
  * @tparam T the type we want to provide an encoder for
  */
trait EncoderProvider[T] extends ConfiguredSerde {

}

trait ConfiguredSerde {
  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults
}

trait NativeDependencies {
  implicit val nativeRequestEncoder: Encoder[NativeRequest]
}

/**
  * Set of features that should be able to manage json openrtb extensions
  */
object OpenRtbExtensions {

  /**
    * An openrtb extension is a generated extension with Optional availability. The [[scalapb.GeneratedExtension]] comes from scalapb stuff
    */
  type OpenRtbExtension[C <: ExtendableMessage[C], T] = GeneratedExtension[C, Option[T]]

  /**
    * An extension handle conveys 4 things
    *
    * @param baseMessage      the class we want to provide an extension for
    * @param extensionMessage the class of the extension
    * @param extension        the [[OpenRtbExtension]] that will serve as a lens
    * @param encoder          the encoder for Extension
    * @param decoder          the decoder for Extension
    * @tparam Extendable the extendable message we want to provide an extension for
    * @tparam Extension  the extension message that will be added to the base message
    *
    */
  case class ExtensionHandle[Extendable <: ExtendableMessage[Extendable], Extension <: GeneratedMessage](
                                                                                                          baseMessage: Class[Extendable],
                                                                                                          extensionMessage: Class[Extension],
                                                                                                          extension: OpenRtbExtension[Extendable, Extension],
                                                                                                          encoder: Encoder[Extension],
                                                                                                          decoder: Decoder[Extension]
                                                                                                        )

  /**
    * An extension registry is a mean to provide encoders based on a list of extensions
    */
  case class ExtensionRegistry(private val extensions: Seq[ExtensionHandle[_ <: ExtendableMessage[_], _]] = Seq()) {

    private val extensionMap: Map[Class[_], Seq[ExtensionHandle[_, _]]] = extensions.groupBy(_.baseMessage)

    /**
      * Register a Extension [[OpenRtbExtension]] for Extendable, using an [[io.circe.Encoder]] and [[io.circe.Decoder]]
      */
    def registerExtension[Extendable <: ExtendableMessage[Extendable], Extension <: scalapb.GeneratedMessage](
                                                                                                               extension: OpenRtbExtension[Extendable, Extension],
                                                                                                               encoder: Encoder[Extension],
                                                                                                               decoder: Decoder[Extension])(implicit extendableTag: ClassTag[Extendable], extensionTag: ClassTag[Extension]): ExtensionRegistry = {

      this.copy(extensions :+ ExtensionHandle[Extendable, Extension](
        extendableTag.runtimeClass.asInstanceOf[Class[Extendable]],
        extensionTag.runtimeClass.asInstanceOf[Class[Extension]],
        extension,
        encoder,
        decoder
      ))

    }

    /**
      * Provide an [[Encoder]] built from the extension registry
      */
    def encoderWithExtensions[Extendable <: ExtendableMessage[Extendable]](baseEncoder: Encoder[Extendable])(implicit tag: ClassTag[Extendable]): Encoder[Extendable] = {
      val extendableHandles = handles(tag)

      // loop through the extendables and perform encoding
      extendableHandles.foldLeft(baseEncoder) {
        case (currentEncoder, handle: ExtensionHandle[Extendable, GeneratedMessage]) =>
          (extendable: Extendable) => {
            val ExtensionHandle(_, _, extension: OpenRtbExtension[Extendable, GeneratedMessage], extEncoder: Encoder[GeneratedMessage], _) = handle

            // encode the extendable entity, then encode and bind the extension too if present
            currentEncoder.apply(extendable)
              .mapObject(obj => {
                val maybeExtension: Option[GeneratedMessage] = extendable.extension(extension.asInstanceOf[OpenRtbExtension[Extendable, GeneratedMessage]])
                alterObject(extEncoder.asInstanceOf[Encoder[GeneratedMessage]])(maybeExtension, obj)
              })
          }
      }
    }

    /**
      * Provide a [[Decoder]] built from the extension registry
      */
    def decoderWithExtensions[Extendable <: ExtendableMessage[Extendable]](baseDecoder: Decoder[Extendable])(implicit tag: ClassTag[Extendable]): Decoder[Extendable] = {
      val extendableHandles = handles(tag)

      // loop through the extendables and perform decoding
      extendableHandles.foldLeft(baseDecoder) {
        case (currentDecoder, handle: ExtensionHandle[Extendable, GeneratedMessage]) =>
          (extendable: HCursor) => {
            val ExtensionHandle(_, _, extension: OpenRtbExtension[Extendable, GeneratedMessage], _, decoder: Decoder[GeneratedMessage]) = handle
            for {
              base <- currentDecoder
              ext <- decoder.prepare(_.downField("ext"))
            } yield base.withExtension(extension)(Some(ext))
          }.apply(extendable)
      }
    }

    private def alterObject(encoder: Encoder[GeneratedMessage])(ext: Option[GeneratedMessage], jsonObject: JsonObject): JsonObject = {
      ext.map(e => jsonObject.add("ext", encoder.apply(e))).getOrElse(jsonObject)
    }


    private def handles[Extendable <: ExtendableMessage[Extendable]](tag: ClassTag[Extendable]) = {
      extensionMap
        .getOrElse(tag.runtimeClass.asInstanceOf[Class[Extendable]], Seq())
        .asInstanceOf[Seq[ExtensionHandle[Extendable, GeneratedMessage]]]
    }
  }

}

