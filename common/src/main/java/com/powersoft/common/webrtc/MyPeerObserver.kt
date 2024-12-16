package com.powersoft.common.webrtc

import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver

open class MyPeerObserver : PeerConnection.Observer{
    override fun onSignalingChange(state: PeerConnection.SignalingState?) {

    }

    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
    }

    override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) {
    }

    override fun onIceCandidate(cadidate: IceCandidate?) {
    }

    override fun onIceCandidatesRemoved(candidateArr: Array<out IceCandidate>?) {
    }

    override fun onAddStream(stream: MediaStream?) {
    }

    override fun onRemoveStream(strean: MediaStream?) {
    }

    override fun onDataChannel(dataChannel: DataChannel?) {
    }

    override fun onRenegotiationNeeded() {
    }

    override fun onAddTrack(rtpReceiver: RtpReceiver?, streamArr: Array<out MediaStream>?) {
    }
}