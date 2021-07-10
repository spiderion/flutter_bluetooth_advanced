package com.example.flutter_bluetooth_advanced

import io.flutter.plugin.common.EventChannel


class StreamHandlerImpl : EventChannel.StreamHandler {
    private var sink: EventChannel.EventSink? = null

    fun getSink(): EventChannel.EventSink? {
        return sink
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        sink = events
    }

    override fun onCancel(arguments: Any?) {
        sink = null
    }
}