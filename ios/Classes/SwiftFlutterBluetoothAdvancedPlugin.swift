import Flutter
import UIKit

public class SwiftFlutterBluetoothAdvancedPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_bluetooth_advanced", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterBluetoothAdvancedPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
