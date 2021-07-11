class BluetoothState {
  final String currentState;
  static const String STATE_OFF = "STATE_OFF";
  static const String STATE_TURNING_ON = "STATE_TURNING_ON";
  static const String STATE_ON = "STATE_ON";
  static const String STATE_TURNING_OFF = "STATE_TURNING_OFF";
  static const String STATE_BLE_TURNING_ON = "STATE_BLE_TURNING_ON";
  static const String STATE_BLE_ON = "STATE_BLE_ON";
  static const String STATE_BLE_TURNING_OFF = "STATE_BLE_TURNING_OFF";
  static const String ERROR = "ERROR";

  BluetoothState(this.currentState);
}
