package com.powersoft.common.utils

import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection.IceServer

object WebRTCUtils {

    fun getMediaConstraints(): MediaConstraints{
        return MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
        }
    }

    fun getIceServers(): List<IceServer>{
        return listOf(
            IceServer.builder("stun:stun.relay.metered.ca:80").createIceServer(),
            IceServer.builder("turn:global.relay.metered.ca:80")
                .setUsername("95d38cf12904f86dad969c49")
                .setPassword("iHgZlyrJTFn8ZXJC")
                .createIceServer(),
            IceServer.builder("turn:global.relay.metered.ca:80?transport=tcp")
                .setUsername("95d38cf12904f86dad969c49")
                .setPassword("iHgZlyrJTFn8ZXJC")
                .createIceServer(),
            IceServer.builder("turn:global.relay.metered.ca:443")
                .setUsername("95d38cf12904f86dad969c49")
                .setPassword("iHgZlyrJTFn8ZXJC")
                .createIceServer(),
            IceServer.builder("turns:global.relay.metered.ca:443?transport=tcp")
                .setUsername("95d38cf12904f86dad969c49")
                .setPassword("iHgZlyrJTFn8ZXJC")
                .createIceServer()
        )
    }
}