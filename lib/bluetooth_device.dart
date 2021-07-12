import 'dart:typed_data';

class BluetoothDevice {
  final String? address;
  final String? name;
  final String? type;
  final Future<bool> Function() connect;
  final Future<bool> Function() disconnect;
  final Function(Uint8List uint8list)? sendRowData;
  final Function(Map<dynamic, dynamic> map)? sendMapData;

  static const String addressKey = 'address';
  static const String nameKey = 'name';
  static const String typeKey = 'type';

  BluetoothDevice({
    required this.address,
    required this.name,
    required this.type,
    required this.connect,
    required this.disconnect,
    required this.sendRowData,
    required this.sendMapData,
  });
}
