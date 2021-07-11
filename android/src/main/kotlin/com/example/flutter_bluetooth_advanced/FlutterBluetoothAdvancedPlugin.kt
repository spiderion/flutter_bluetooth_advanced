package com.example.flutter_bluetooth_advanced

import android.content.Context
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** FlutterBluetoothAdvancedPlugin */
class FlutterBluetoothAdvancedPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private var context: Context? = null
    private lateinit var eventChannel: EventChannel
    private var streamHandler: StreamHandlerImpl = StreamHandlerImpl()
    private var bluetoothManager: BluetoothManager? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_bluetooth_advanced")
        channel.setMethodCallHandler(this)
        eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_bluetooth_events")
        eventChannel.setStreamHandler(streamHandler)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            MethodNameKeys.scan -> onScan(result)
            MethodNameKeys.cancelScan -> onCancelScan(result)
            MethodNameKeys.connect -> onConnect(call.arguments as String, result)
            MethodNameKeys.disconnect -> onDisconnect(call.arguments as String, result)
            MethodNameKeys.disconnectAll -> onDisconnectAll(result)
            MethodNameKeys.listenBluetoothState -> onListenBluetoothState(result)
            else -> result.notImplemented()

        }
    }

    private fun onCancelScan(result: Result) {
        bluetoothManager?.cancelScanDevices {
            if (it) result.success("cancelled")
            else
                result.error("CANCEL_SCAN_ERROR", "Could not cancel scan", "")
        }
    }

    private fun onConnect(address: String, result: Result) {
        bluetoothManager?.connectToDevice(address, {
            result.success(true)
        }, {
            result.error("COULD_NOT_CONNECT_ERROR", "Could not connect", it);
        })
    }

    private fun onDisconnect(address: String, result: Result) {
        bluetoothManager?.disconnect(address, {
            result.success(true)
        }, {
            result.error("COULD_NOT_DISCONNECT_ERROR", it, it)
        })
    }

    private fun onDisconnectAll(result: Result) {
        bluetoothManager?.disconnectAll({
            result.success(true)
        }, {
            result.error("COULD_NOT_DISCONNECT_ERROR", it, it)
        })
    }

    private fun onListenBluetoothState(result: Result) {
        bluetoothManager?.listenBluetoothState(object : BluetoothStateChangeListener {
            override fun onStateChanged(bluetoothState: String?) {
                sendEventToFlutter(EventChannelTypes.bluetoothStateChanged, bluetoothState)
            }
        })
    }

    private fun onScan(result: Result) {
        bluetoothManager?.scanDevices(object : ScanEventListener {
            override fun onDeviceFound(device: Map<String, Any?>) {
                sendEventToFlutter(EventChannelTypes.deviceScanFound, device)
            }

            override fun onScanStarted() {
                sendEventToFlutter(EventChannelTypes.scanStarted, null)
            }

            override fun onScanFinished() {
                sendEventToFlutter(EventChannelTypes.scanFinished, null)
            }

            override fun onError(errorMessage: String) {
                result.error("SCAN_ERROR", errorMessage, "")
            }

        })
    }


    private fun sendEventToFlutter(type: String, data: Any?) {
        val map = mapOf("type" to type, "data" to data)
        streamHandler.getSink()?.success(map)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        context = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        bluetoothManager = BluetoothManager(binding.activity)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        bluetoothManager = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        bluetoothManager = BluetoothManager(binding.activity)
    }

    override fun onDetachedFromActivity() {
        bluetoothManager = null
    }
}
