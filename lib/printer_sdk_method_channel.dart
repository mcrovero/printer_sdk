import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'printer_sdk_platform_interface.dart';

/// An implementation of [PrinterSdkPlatform] that uses method channels.
class MethodChannelPrinterSdk extends PrinterSdkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('printer_sdk');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> checkUsbDriver() async {
    final result = await methodChannel.invokeMethod<bool>('checkUsbDriver');
    return result ?? false;
  }

  @override
  Future<String?> checkDevices() async {
    final result = await methodChannel.invokeMethod<String>('checkDevices');
    return result;
  }

  @override
  Future<bool> printSelfCheck() async {
    final result = await methodChannel.invokeMethod<bool>('printSelfCheck');
    return result ?? false;
  }
}
