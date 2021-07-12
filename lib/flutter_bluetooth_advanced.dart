import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';
import 'package:flutter_bluetooth_advanced/bluetooth_device.dart';
import 'package:flutter_bluetooth_advanced/scanner_call_back_events.dart';

import 'bluetooth_state.dart';
import 'event_channel_types.dart';
import 'map_converter.dart';

class FlutterBluetoothAdvanced {
  static const String _data = 'data';
  static const String _type = 'type';
  static const MethodChannel _channel = const MethodChannel('flutter_bluetooth_advanced');
  static const EventChannel _eventChannel = const EventChannel(createEventChannelMethodName);
  static const String createEventChannelMethodName = 'flutter_bluetooth_events';
  StreamSubscription? _subscription;
  ScannerCallBackEvents? _scanEvents;
  final MapConverter _mapConverter = MapConverter();
  StreamController<BluetoothState>? _bluetoothStateController;

  _shouldInit() async {
    _subscription ??= _registerNativeEvents();
  }

  StreamSubscription<dynamic> _registerNativeEvents() {
    return _eventChannel.receiveBroadcastStream().listen((dynamic event) {
      if (_getEventType(event) == EventChannelTypes.deviceScanFound) {
        final device = _createDeviceFromData(_getEventData(event));
        _scanEvents?.onDeviceFound?.call(device);
      } else if (_getEventType(event) == EventChannelTypes.scanStarted) {
        _scanEvents?.onScanStarted?.call();
      } else if (_getEventType(event) == EventChannelTypes.scanFinished) {
        _scanEvents?.onScanFinished?.call();
      } else if (_getEventType(event) == EventChannelTypes.bluetoothStateChanged) {
        _bluetoothStateController?.add(BluetoothState(_getEventData(event)));
      }
    });
  }

  Stream<BluetoothState> registerBluetoothStateChange() {
    _shouldInit();
    _createBluetoothStreamController();
    return _bluetoothStateController!.stream;
  }

  void _createBluetoothStreamController() {
    _bluetoothStateController ??= StreamController<BluetoothState>(onListen: () {
      _channel.invokeMethod(MethodNameKeys.listenBluetoothState);
    });
  }

  BluetoothDevice _createDeviceFromData(dynamic map) {
    final address = map[BluetoothDevice.addressKey];
    final name = map[BluetoothDevice.nameKey];
    final type = map[BluetoothDevice.typeKey];
    return BluetoothDevice(
      address: address,
      name: name,
      type: type,
      connect: () => _connect(address),
      disconnect: () => _disconnect(address),
      sendRowData: (Uint8List data) => sendRowData(address, data),
      sendMapData: (Map<dynamic, dynamic> data) =>
          sendRowData(address, _mapConverter.getUint8ListFromMapData(data)),
    );
  }

  String? _getEventType(event) => (event as Map)[_type];

  dynamic _getEventData(event) => (event as Map)[_data];

  void scan(ScannerCallBackEvents callBackEvents) async {
    _scanEvents = callBackEvents;
    _shouldInit();
    _channel.invokeMethod(MethodNameKeys.scan);
  }

  void dispose() async {
    disconnectAll();
    _subscription?.cancel();
    _bluetoothStateController?.close();
    _subscription = null;
  }

  void sendRowData(String address, Uint8List data) async {
    final args = {BluetoothDevice.addressKey: address, _data: data};
    await _channel.invokeMethod(MethodNameKeys.sendData, args);
  }

  Future<bool> _connect(String address) async {
    final hasConnected = await _channel.invokeMethod(MethodNameKeys.connect, address);
    return hasConnected;
  }

  Future<bool> _disconnect(String address) async {
    final hasDisconnected = await _channel.invokeMethod(MethodNameKeys.disconnect, address);
    return hasDisconnected;
  }

  void disconnectAll() {
    _channel.invokeMethod(MethodNameKeys.disconnectAll);
  }
}
