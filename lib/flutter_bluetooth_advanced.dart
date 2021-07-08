import 'dart:async';

import 'package:flutter/services.dart';

class FlutterBluetoothAdvanced {
  static const MethodChannel _channel = const MethodChannel('flutter_bluetooth_advanced');
  static const EventChannel _eventChannelScan = const EventChannel('scan_devices');

  Future<String?> getPlatformVersion() async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Stream<dynamic> scan() async* {
    print('start scanning');
    final String? version = await _channel.invokeMethod('scan');
  }

/* Stream<dynamic> scan() async* {
    late StreamSubscription subscription;
    StreamController controller;

    controller = new StreamController(
      onCancel: () {
        // `cancelDiscovery` happens automaticly by platform code when closing event sink
        subscription.cancel();
      },
    );

    // await _methodChannel.invokeMethod('startDiscovery');

    subscription = _discoveryChannel.receiveBroadcastStream().listen(
          controller.add,
          onError: controller.addError,
          onDone: controller.close,
        );

    // yield* controller.stream.map((map) => BluetoothDiscoveryResult.fromMap(map));
  }*/
}
