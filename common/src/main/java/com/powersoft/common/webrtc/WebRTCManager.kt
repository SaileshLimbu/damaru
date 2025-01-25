package com.powersoft.common.webrtc

import android.content.Context
import android.util.Log
import com.powersoft.common.webrtc.WebRTCClient.Companion.TAG
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import java.util.UUID
import javax.inject.Inject

class WebRTCManager @Inject constructor(context: Context) {
    private val eglBase: EglBase = EglBase.create()

    private val peerConnectionFactory: PeerConnectionFactory

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
                .createInitializationOptions()
        )

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .setOptions(PeerConnectionFactory.Options().apply {
                disableEncryption = false
                disableNetworkMonitor = false
            }).createPeerConnectionFactory()
    }

    fun getEglBase() = eglBase

    fun getMyPeerConnectionFactory() = peerConnectionFactory

    fun createVideoSource(): VideoSource {
        return peerConnectionFactory.createVideoSource(true)
    }

    fun createPeerConnection(
        iceServers: List<PeerConnection.IceServer>,
        observer: PeerConnection.Observer
    ): PeerConnection? {
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        return peerConnectionFactory.createPeerConnection(rtcConfig, observer)
    }

    fun createVideoTrack(videoSource: VideoSource): VideoTrack {
        return peerConnectionFactory.createVideoTrack("VIDEO${UUID.randomUUID()}", videoSource)
    }

    fun dispose(){
        try {
            eglBase.release()
            peerConnectionFactory.dispose()
            Log.d(TAG, "Clearing EglBase and PeerConnectionFactory", )
        }catch (e: Exception){
            //ignore
        }
    }
}