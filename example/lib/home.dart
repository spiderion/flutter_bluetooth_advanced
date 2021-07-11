import 'package:flutter/material.dart';
import 'package:flutter_bluetooth_advanced/bluetooth_device.dart';
import 'package:flutter_bluetooth_advanced/bluetooth_state.dart';
import 'package:flutter_bluetooth_advanced/flutter_bluetooth_advanced.dart';
import 'package:flutter_bluetooth_advanced/scanner_call_back_events.dart';

import 'device_page.dart';

class HomePage extends StatefulWidget {
  final FlutterBluetoothAdvanced flutterBluetoothAdvanced;

  const HomePage({Key? key, required this.flutterBluetoothAdvanced}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final devices = <BluetoothDevice>[];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      floatingActionButton: _scanButton(),
      appBar: AppBar(
        centerTitle: true,
        title: Column(
          children: [Text('Devices'), bluetoothState()],
        ),
      ),
      body: Column(
        children: [Expanded(child: getDevices())],
      ),
    );
  }

  Widget bluetoothState() {
    return StreamBuilder(
        stream: widget.flutterBluetoothAdvanced.registerBluetoothStateChange(),
        builder: (context, AsyncSnapshot<BluetoothState> snapshot) {
          final result = (snapshot.data?.currentState ?? "unknown");
          return Center(
              child: Text(
            'bluetooth ' + result,
            style: TextStyle(fontSize: 13),
          ));
        });
  }

  Widget _scanButton() {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.only(bottom: 24.0),
        child: ElevatedButton(
            style: buttonStyle(),
            onPressed: () {
              devices.clear();
              widget.flutterBluetoothAdvanced
                  .scan(ScannerCallBackEvents(onDeviceFound: (BluetoothDevice device) {
                devices.add(device);
                removeDuplications();
                setState(() {});
              }, onError: (String error) {
                print('on error: $error');
              }, onScanFinished: () {
                print('onScanFinished');
              }));
            },
            child: Text('SCAN')),
      ),
    );
  }

  ButtonStyle buttonStyle() {
    return ButtonStyle(
        shape: MaterialStateProperty.all(RoundedRectangleBorder(borderRadius: BorderRadius.circular(15))));
  }

  void removeDuplications() {
    final addresses = devices.map((BluetoothDevice e) => e.address!).toSet().toList();
    devices.retainWhere((x) => addresses.remove(x.address));
  }

  ListView getDevices() {
    return ListView(children: devices.map((e) => itemDevice(e)).toList());
  }

  Widget itemDevice(BluetoothDevice device) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: ListTile(
        title: Text(device.name ?? 'Unknown Name', style: TextStyle(color: Colors.green)),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(device.type ?? '', style: TextStyle(color: Colors.white)),
            SizedBox(height: 2),
            Text(device.address ?? '', style: TextStyle(fontSize: 12)),
          ],
        ),
        trailing: ElevatedButton(
          style: buttonStyle().copyWith(backgroundColor: MaterialStateProperty.all(Colors.orange)),
          onPressed: () {
            device.connect().then((isConnected) {
              if (isConnected == true) {
                navigateToDevicePage(device).then((value) => device.disconnect());
              } else {
                print('could not connect to device');
              }
            });
          },
          child: Text('CONNECT'),
        ),
      ),
    );
  }

  Future<dynamic> navigateToDevicePage(BluetoothDevice device) {
    return Navigator.of(context)
        .push(MaterialPageRoute(builder: (context) => DevicePage(bluetoothDevice: device)));
  }

  @override
  void dispose() {
    widget.flutterBluetoothAdvanced.dispose();
    super.dispose();
  }
}
