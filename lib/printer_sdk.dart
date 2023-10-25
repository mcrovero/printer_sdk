import 'printer_sdk_platform_interface.dart';

class PrinterSdk {
  Future<String?> getPlatformVersion() {
    return PrinterSdkPlatform.instance.getPlatformVersion();
  }

  Future<bool> checkUsbDriver() {
    return PrinterSdkPlatform.instance.checkUsbDriver();
  }

  Future<String?> checkDevices() {
    return PrinterSdkPlatform.instance.checkDevices();
  }

  Future<bool> printSelfCheck() {
    return PrinterSdkPlatform.instance.printSelfCheck();
  }
}
