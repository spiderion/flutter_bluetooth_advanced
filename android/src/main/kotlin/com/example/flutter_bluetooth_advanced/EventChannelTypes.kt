package com.example.flutter_bluetooth_advanced

class EventChannelTypes {
    companion object {
        const val deviceScanFound = "deviceScanFound"
        const val scanStarted = "scanStarted"
        const val scanFinished = "scanFinished"
    }
}

class MethodNameKeys {
    companion object {
        const val scan = "scan"
        const val cancelScan = "cancelScan"
        const val connect = "connect"
        const val disconnect = "disconnect"
        const val disconnectAll = "disconnectAll"
    }
}

class DeviceTypes {
    companion object {
        val deviceTypes = arrayListOf<DeviceType>(
                DeviceType(0, "DEVICE_TYPE_UNKNOWN"),
                DeviceType(1, "DEVICE_TYPE_CLASSIC"),
                DeviceType(2, "DEVICE_TYPE_LE"),
                DeviceType(3, "DEVICE_TYPE_DUAL")
        )
    }
}

data class DeviceType(val typeCode: Int, val text: String)