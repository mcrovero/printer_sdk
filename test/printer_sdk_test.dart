import 'package:flutter_test/flutter_test.dart';
import 'package:printer_sdk/printer_sdk.dart';
import 'package:printer_sdk/printer_sdk_platform_interface.dart';
import 'package:printer_sdk/printer_sdk_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPrinterSdkPlatform
    with MockPlatformInterfaceMixin
    implements PrinterSdkPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final PrinterSdkPlatform initialPlatform = PrinterSdkPlatform.instance;

  test('$MethodChannelPrinterSdk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPrinterSdk>());
  });

  test('getPlatformVersion', () async {
    PrinterSdk printerSdkPlugin = PrinterSdk();
    MockPrinterSdkPlatform fakePlatform = MockPrinterSdkPlatform();
    PrinterSdkPlatform.instance = fakePlatform;

    expect(await printerSdkPlugin.getPlatformVersion(), '42');
  });
}
