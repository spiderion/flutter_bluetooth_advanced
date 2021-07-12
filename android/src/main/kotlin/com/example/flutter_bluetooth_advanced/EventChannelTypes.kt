package com.example.flutter_bluetooth_advanced

import android.bluetooth.BluetoothDevice

class EventChannelTypes {
    companion object {
        const val deviceScanFound = "deviceScanFound"
        const val scanStarted = "scanStarted"
        const val scanFinished = "scanFinished"
        const val bluetoothStateChanged = "bluetoothStateChanged"
    }
}

class MapperKeys {
    companion object {
        const val data = "data"
        const val type = "type"
        const val address = "address"
    }
}

class MethodNameKeys {
    companion object {
        const val scan = "scan"
        const val cancelScan = "cancelScan"
        const val connect = "connect"
        const val disconnect = "disconnect"
        const val disconnectAll = "disconnectAll"
        const val listenBluetoothState = "listenBluetoothState"
        const val sendData = "sendData"
    }
}

class DeviceTypes {
    companion object {
        val deviceTypes = arrayListOf(
                DeviceType(0, "DEVICE_TYPE_UNKNOWN"),
                DeviceType(1, "DEVICE_TYPE_CLASSIC"),
                DeviceType(2, "DEVICE_TYPE_LE"),
                DeviceType(3, "DEVICE_TYPE_DUAL")
        )
    }
}

data class DeviceType(val typeCode: Int, val text: String)

class BluetoothStates {
    companion object {
        val bluetoothStates = arrayListOf(
                BluetoothState(10, "STATE_OFF"),
                BluetoothState(11, "STATE_TURNING_ON"),
                BluetoothState(12, "STATE_ON"),
                BluetoothState(13, "STATE_TURNING_OFF"),
                BluetoothState(14, "STATE_BLE_TURNING_ON"),
                BluetoothState(15, "STATE_BLE_ON"),
                BluetoothState(16, "STATE_BLE_TURNING_OFF"),
                BluetoothState(BluetoothDevice.ERROR, "ERROR")
        )
    }
}

data class BluetoothState(val intState: Int, val stringState: String)