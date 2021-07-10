import 'package:flutter/material.dart';
import 'package:flutter_bluetooth_advanced/flutter_bluetooth_advanced.dart';
import 'package:flutter_bluetooth_advanced_example/home.dart';

void main() {
  final lib = FlutterBluetoothAdvanced();
  runApp(MaterialApp(theme: ThemeData.dark(), home: HomePage(flutterBluetoothAdvanced: lib)));
}
