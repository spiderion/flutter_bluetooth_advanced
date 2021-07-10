class BluetoothDevice {
  final String? address;
  final String? name;
  final String? type;
  final Future<bool> Function() connect;
  final Future<bool> Function() disconnect;

  static const String addressKey = 'address';
  static const String nameKey = 'name';
  static const String typeKey = 'type';

  BluetoothDevice({
    required this.address,
    required this.name,
    required this.type,
    required this.connect,
    required this.disconnect,
  });
}
