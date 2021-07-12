package com.example.flutter_bluetooth_advanced

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.io.IOException
import java.security.InvalidParameterException
import java.util.*
import java.util.concurrent.Executors

class SerialSocket(context: Context, device: BluetoothDevice) : Runnable {
    private val disconnectBroadcastReceiver: BroadcastReceiver
    private val context: Context
    private var listener: SerialListener? = null
    val device: BluetoothDevice
    private var socket: BluetoothSocket? = null
    private var connected = false
    val name: String
        get() = if (device.name != null) device.name else device.address

    /**
     * connect-success and most connect-errors are returned asynchronously to listener
     */
    fun connect(listener: SerialListener?) {
        this.listener = listener
        context.registerReceiver(disconnectBroadcastReceiver, IntentFilter(".Disconnect"))
        socket = device.createRfcommSocketToServiceRecord(BLUETOOTH_SPP)
        Executors.newSingleThreadExecutor().submit(this)
    }

    fun disconnect() {
        listener = null // ignore remaining data and errors
        // connected = false; // run loop will reset connected
        if (socket != null) {
            try {
                socket!!.close()
            } catch (ignored: Exception) {
            }
            socket = null
        }
        try {
            context.unregisterReceiver(disconnectBroadcastReceiver)
        } catch (ignored: Exception) {
        }
    }

    @Throws(IOException::class)
    fun write(data: ByteArray?) {
        if (!connected) throw IOException("not connected")
        socket!!.outputStream.write(data)
    }

    override fun run() { // connect & read
        try {
            socket?.connect()
            listener?.onSerialConnect()
        } catch (e: Exception) {
            listener?.onSerialConnectError(e)
            try {
                socket!!.close()
            } catch (ignored: Exception) {
            }
            socket = null
            return
        }
        connected = true
        try {
            val buffer = ByteArray(1024)
            var len: Int?
            while (true) {
                len = socket?.inputStream?.read(buffer)
                val data = buffer.copyOf(len ?: 0)
                listener?.onSerialRead(data)
            }
        } catch (e: Exception) {
            connected = false
            listener?.onSerialIoError(e)
            try {
                socket?.close()
            } catch (ignored: Exception) {
            }
            socket = null
        }
    }

    fun isConnected(): Boolean {
        return socket?.isConnected ?: false
    }

    companion object {
        private val BLUETOOTH_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    init {
        if (context is Activity) throw InvalidParameterException("expected non UI context")
        this.context = context
        this.device = device
        disconnectBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                listener?.onSerialIoError(IOException("background disconnect"))
                disconnect() // disconnect now, else would be queued until UI re-attached
            }
        }
    }
}

interface SerialListener {
    fun onSerialConnect()
    fun onSerialConnectError(e: java.lang.Exception?)
    fun onSerialRead(data: ByteArray?)
    fun onSerialIoError(e: java.lang.Exception?)
}
