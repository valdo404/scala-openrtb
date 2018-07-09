package com.powerspace.openrtb.json

import com.powerspace.openrtb.json.OpenRtbExtensions.ExtensionRegistry

/**
  * Provides serialization and deserialization for OpenRTB entities.
  */
class OpenRtbSerdeModule extends SerdeModule {

  override implicit def extensionRegistry: ExtensionRegistry = ExtensionRegistry()

}
