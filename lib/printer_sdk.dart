import 'printer_sdk_platform_interface.dart';

class PrinterSdk {
  Future<String?> getPlatformVersion() {
    return PrinterSdkPlatform.instance.getPlatformVersion();
  }

  Future<bool> checkUsbDriver() {
    return PrinterSdkPlatform.instance.checkUsbDriver();
  }
}
