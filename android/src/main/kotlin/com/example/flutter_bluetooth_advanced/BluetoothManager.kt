package com.example.flutter_bluetooth_advanced

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log

class BluetoothManager(private val activity: Activity) {
    private var currentScanReceiver: BroadcastReceiver? = null
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val connectionThreads = arrayListOf<ConnectionThread>()

    fun scanDevices(callBackEvents: ScannerCallBackEvents) {
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        checkPermissions()
        unregisterCurrentScanReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(ACTION_DISCOVERY_STARTED);
        currentScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when {
                    intent?.action?.equals(ACTION_DISCOVERY_STARTED) == true -> {
                        callBackEvents.onScanStarted()
                    }
                    intent?.action?.equals(BluetoothDevice.ACTION_FOUND) == true -> {
                        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        val deviceMap = mapOf(
                                "address" to device?.address,
                                "name" to device?.name,
                                "type" to getDeviceType(device)?.text
                        )
                        callBackEvents.onDeviceFound(deviceMap)
                    }
                    intent?.action?.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) == true -> {
                        unregisterCurrentScanReceiver()
                        callBackEvents.onScanFinished();
                    }
                }
            }

        }
        activity.registerReceiver(currentScanReceiver, intentFilter)
        val result = bluetoothAdapter.startDiscovery()
        val state = bluetoothAdapter.state
        //Toast.makeText(activity, "isDiscovering: $result , State: $state", Toast.LENGTH_LONG).show();
    }

    fun connectToDevice(address: String, onConnected: () -> Unit, onErrorResult: (error: String) -> Unit) {
        val device = bluetoothAdapter.getRemoteDevice(address)
        val connectionThread = ConnectionThread(device, null, object : ConnectionCallbacks {
            override fun preInitConnection() {
                bluetoothAdapter.cancelDiscovery()
            }

            override fun onConnected(socket: BluetoothSocket) {
                onConnected()
            }

            override fun onError(message: String?) {
                onErrorResult(message ?: "")
            }
        })
        connectionThreads.removeAll { !it.isConnected() }
        connectionThreads.add(connectionThread)
        connectionThread.run()
    }

    private fun unregisterCurrentScanReceiver() {
        try {
            currentScanReceiver?.let {
                activity.unregisterReceiver(it)
                currentScanReceiver = null
            }
        } catch (ex: IllegalArgumentException) {
            // Ignore `Receiver not registered` exception
        }
    }

    fun disconnect(address: String, onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        val foundConnection = connectionThreads.find { it.getSocket()?.remoteDevice?.address == address }
        val device = foundConnection?.getDevice();
        Log.i(TAG, "disconnect: disconnect socket with name: ${device?.name}  address: ${device?.address}")
        foundConnection?.cancel(onSuccess, onError)
        connectionThreads.remove(foundConnection)
    }

    fun disconnectAll(onSuccess: () -> Unit, onError: (message: String) -> Unit) {
        Log.i(TAG, "disconnectAll: disconnect all sockets -> sockets size: ${connectionThreads.size}")
        connectionThreads.forEach { it.cancel(onSuccess, onError) }
    }

    fun getDeviceType(device: BluetoothDevice?): DeviceType? {
        return DeviceTypes.deviceTypes.find {
            it.typeCode == if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) device?.type else 0
        }
    }

    fun cancelScanDevices(onCanceled: (isCanceled: Boolean) -> Unit) {
        onCanceled(bluetoothAdapter.cancelDiscovery())
    }


    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var permissionCheck = activity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
            permissionCheck += activity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
            if (permissionCheck != 0) {
                val permissions = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
                val requestCode = 102
                activity.requestPermissions(permissions, requestCode)
            }
        } else {
            print("no need to check permissions SDK version is less than LOLLIPOP")
        }
    }
}

interface ScannerCallBackEvents {
    fun onDeviceFound(device: Map<String, Any?>)

    fun onScanStarted()

    fun onScanFinished()

    fun onError(errorMessage: String)
}