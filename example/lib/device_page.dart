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
      appBar: AppBar(
          title: Column(
            children: [
              Text(widget.bluetoothDevice.name ?? "Unknown Name"),
              Text(
                widget.bluetoothDevice.type ?? "Unknown Type",
                style: TextStyle(fontSize: 12),
              ),
            ],
          ),
          centerTitle: true),
      body: Container(
        alignment: Alignment.center,
        child: Column(
          children: [
            Expanded(
                child: Center(
              child: Text(widget.bluetoothDevice.name ?? 'Unknown Name'),
            )),
            bottomButtons(),
          ],
        ),
      ),
    );
  }

  Widget bottomButtons() {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.all(14.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            getButton(() {
              print("flutter sent on" + DateTime.now().toString());
              widget.bluetoothDevice.sendMapData?.call(allOnText());
            }, "SEND DATA"),
            SizedBox(width: 30),
            getButton(() {
              print("flutter sent off" + DateTime.now().toString());
              widget.bluetoothDevice.sendMapData?.call(allOffText());
            }, "SEND OFF"),
          ],
        ),
      ),
    );
  }

  Map<String, dynamic> allOnText() {
    return {
      "commands": [
        {"pinKey": 19, "pinWrite": 1},
        {"pinKey": 18, "pinWrite": 1},
        {"pinKey": 23, "pinWrite": 1},
        {"pinKey": 22, "pinWrite": 1},
        {"pinKey": 21, "pinWrite": 1},
      ]
    };
  }

  Map<String, dynamic> allOffText() {
    return {
      "commands": [
        {"pinKey": 19, "pinWrite": 0},
        {"pinKey": 18, "pinWrite": 0},
        {"pinKey": 23, "pinWrite": 0},
        {"pinKey": 22, "pinWrite": 0},
        {"pinKey": 21, "pinWrite": 0},
      ]
    };
  }

  ElevatedButton getButton(Function() onPressed, String text) {
    return ElevatedButton(style: buttonStyle(), onPressed: onPressed, child: Text(text));
  }

  ButtonStyle buttonStyle() {
    return ButtonStyle(
        shape: MaterialStateProperty.all(RoundedRectangleBorder(borderRadius: BorderRadius.circular(15))));
  }
}
