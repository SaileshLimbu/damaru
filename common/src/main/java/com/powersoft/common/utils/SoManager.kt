package com.powersoft.common.utils

class SoManager {
    companion object {
        init {
            System.loadLibrary("KeyManager")
        }
    }

    external fun getPublicKey(): String
}
