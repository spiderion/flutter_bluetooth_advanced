import 'package:flutter/material.dart';
import 'package:flutter_bluetooth_advanced/flutter_bluetooth_advanced.dart';

class MyApp extends StatefulWidget {
  final FlutterBluetoothAdvanced flutterBluetoothAdvanced;

  const MyApp({Key? key, required this.flutterBluetoothAdvanced}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData.dark(),
      home: Scaffold(
        floatingActionButton: _scanButton(),
        appBar: AppBar(centerTitle: true, title: Text('Devices')),
        body: Column(
          children: [Expanded(child: getDevices())],
        ),
      ),
    );
  }

  Widget _scanButton() {
    return SafeArea(
      child: Padding(
        padding: const EdgeInsets.only(bottom: 24.0),
        child: ElevatedButton(
            style: ButtonStyle(
                shape: MaterialStateProperty.all(
                    RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)))),
            onPressed: () {
              widget.flutterBluetoothAdvanced.scan();
            },
            child: Text('Scan')),
      ),
    );
  }

  ListView getDevices() {
    return ListView(children: []);
  }
}
