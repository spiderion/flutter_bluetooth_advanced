import 'package:flutter/material.dart';
import 'package:flutter_bluetooth_advanced/bluetooth_device.dart';

class DevicePage extends StatefulWidget {
  final BluetoothDevice bluetoothDevice;

  const DevicePage({Key? key, required this.bluetoothDevice}) : super(key: key);

  @override
  _DevicePageState createState() => _DevicePageState();
}

class _DevicePageState extends State<DevicePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        alignment: Alignment.center,
        child: Text(widget.bluetoothDevice.name ?? 'Unknown Name'),
      ),
    );
  }
}
