import 'package:flutter_bluetooth_advanced/bluetooth_device.dart';

class ScannerCallBackEvents {
  Function(BluetoothDevice)? onDeviceFound;
  Function()? onScanStarted;
  Function()? onScanFinished;
  Function(String message)? onError;

  ScannerCallBackEvents({
    required this.onDeviceFound,
    this.onScanStarted,
    this.onScanFinished,
    this.onError,
  });
}