package com.example.flutter_bluetooth_advanced

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import java.io.IOException
import java.util.*


class ConnectionThread(
        device: BluetoothDevice,
        customUUID: String?,
        private val connectionCallbacks: ConnectionCallbacks) : Thread() {

    private val defaultUUID: String = "00001101-0000-1000-8000-00805F9B34FB"
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(createUUID(customUUID ?: defaultUUID))
    }

    private fun createUUID(uuid: String?): UUID {
        return UUID.fromString(uuid)
    }

    override fun run() {
        try {
            connectionCallbacks.preInitConnection()
            mmSocket?.let { socket ->
                socket.connect()
                connectionCallbacks.onConnected(socket)
            }
        } catch (e: Exception) {
            connectionCallbacks.onError(e.message)
        }
    }

    fun getSocket(): BluetoothSocket? {
        return mmSocket
    }

    fun cancel(onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        try {
            mmSocket?.close()
            onSuccess()
        } catch (e: IOException) {
            val error = "Could not close the client socket"
            Log.e(TAG, error, e)
            onError(error + " " + e.message)
        }
    }

    fun write(bytes: ByteArray, onError: (errorMessage: String) -> Unit) {
        try {
            mmSocket?.outputStream?.write(bytes)
        } catch (e: IOException) {
            onError(e.message ?: "error sending data to device")
            return
        }
    }

    fun getDevice(): BluetoothDevice? {
        return mmSocket?.remoteDevice
    }

    fun isConnected(): Boolean {
        return mmSocket?.isConnected ?: false
    }
}

interface ConnectionCallbacks {
    fun preInitConnection()
    fun onConnected(socket: BluetoothSocket)
    fun onError(message: String?)
}