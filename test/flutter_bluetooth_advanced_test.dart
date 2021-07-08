import 'package:flutter/services.dart';
import 'package:flutter_bluetooth_advanced/flutter_bluetooth_advanced.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  FlutterBluetoothAdvanced sut = FlutterBluetoothAdvanced();
  const MethodChannel channel = MethodChannel('flutter_bluetooth_advanced');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await sut.getPlatformVersion(), '42');
  });
}
