import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:printer_sdk/printer_sdk.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _printerStatus = 'Unknown';
  String _devicesStatus = 'Unknown';

  final _printerSdkPlugin = PrinterSdk();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _printerSdkPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(children: [
          Text('Running on: $_platformVersion\n'),
          Text('Printer Status: $_printerStatus\n'),
          ElevatedButton(
            onPressed: () async {
              var usbDriverState = await _printerSdkPlugin.checkUsbDriver();
              setState(() {
                _printerStatus = usbDriverState ? 'Connected' : 'Disconnected';
              });
            },
            child: const Text('Check USB Driver'),
          ),
          Text('Devices Status: $_devicesStatus\n'),
          ElevatedButton(
            onPressed: () async {
              var devicesState = await _printerSdkPlugin.checkDevices();
              setState(() {
                _devicesStatus = devicesState ?? 'Unknown';
              });
            },
            child: const Text('Check devices'),
          ),
          ElevatedButton(
              onPressed: () async {
                var printSelfCheckState =
                    await _printerSdkPlugin.printSelfCheck();
                setState(() {
                  _printerStatus = printSelfCheckState ? 'Success' : 'Failed';
                });
              },
              child: const Text('Print Self Check'))
        ]),
      ),
    );
  }
}
