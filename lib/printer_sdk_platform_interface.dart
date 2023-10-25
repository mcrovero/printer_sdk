import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'printer_sdk_method_channel.dart';

abstract class PrinterSdkPlatform extends PlatformInterface {
  /// Constructs a PrinterSdkPlatform.
  PrinterSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static PrinterSdkPlatform _instance = MethodChannelPrinterSdk();

  /// The default instance of [PrinterSdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelPrinterSdk].
  static PrinterSdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PrinterSdkPlatform] when
  /// they register themselves.
  static set instance(PrinterSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool> checkUsbDriver() {
    throw UnimplementedError('checkUsbDriver() has not been implemented.');
  }
}
