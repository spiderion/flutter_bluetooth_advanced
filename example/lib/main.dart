import 'package:flutter/material.dart';
import 'package:flutter_bluetooth_advanced/flutter_bluetooth_advanced.dart';

import 'app.dart';

void main() {
  final lib = FlutterBluetoothAdvanced();
  runApp(MyApp(flutterBluetoothAdvanced: lib));
}
