package com.ujiboo.printer_sdk;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.msprintsdk.PrintCmd;
import com.msprintsdk.UsbDriver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/** PrinterSdkPlugin */
public class PrinterSdkPlugin implements FlutterPlugin, MethodCallHandler {
  private static final String ACTION_USB_PERMISSION = "com.usb.sample.USB_PERMISSION";

  /// The MethodChannel that will the communication between Flutter and native
  /// Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine
  /// and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;

  static UsbDriver mUsbDriver;
  UsbDevice mUsbDevice = null;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "printer_sdk");
    channel.setMethodCallHandler(this);

    context = flutterPluginBinding.getApplicationContext();

    mUsbDriver = new UsbDriver((UsbManager) context.getSystemService(Context.USB_SERVICE), context);
    PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
    mUsbDriver.setPermissionIntent(permissionIntent);

    IntentFilter filter = new IntentFilter();
    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
    filter.addAction(ACTION_USB_PERMISSION);
    context.registerReceiver(mUsbReceiver, filter);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("checkUsbDriver")) {
      result.success(usbDriverCheck() == 0);
    } else if (call.method.equals("checkDevices")) {
      try {
        result.success(checkDevices());
      } catch (IOException e) {
        result.error("IOException", e.getMessage(), e);
      }
    } else if (call.method.equals("printSelfCheck")) {
      try {
        if(usbDriverCheck() != 0) {
          result.error("IOException", "usbDriverCheck failed", null);
          return;
        }
        mUsbDriver.write(PrintCmd.PrintSelfcheck());
        result.success(true);
      } catch (Exception e) {
        result.error("IOException", e.getMessage(), e);
      }
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  public int usbDriverCheck() {
    int iResult = -1;
    try {
      if (!mUsbDriver.isUsbPermission()) {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        mUsbDevice = null;
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
          UsbDevice device = deviceIterator.next();
          if ((device.getProductId() == 8211 && device.getVendorId() == 1305)
              || (device.getProductId() == 8213 && device.getVendorId() == 1305)) {
            mUsbDevice = device;
            // ShowMessage("DeviceClass:" + device.getDeviceClass() + ";DeviceName:" +
            // device.getDeviceName());
          }
        }
        if (mUsbDevice != null) {
          iResult = 1;
          if (mUsbDriver.usbAttached(mUsbDevice)) {
            if (mUsbDriver.openUsbDevice(mUsbDevice))
              iResult = 0;
          }
        }
      } else {
        if (!mUsbDriver.isConnected()) {
          if (mUsbDriver.openUsbDevice(mUsbDevice))
            iResult = 0;
        } else {
          iResult = 0;
        }
      }
    } catch (Exception e) {

      Log.e(TAG, "usbDriverCheck:" + e.getMessage());
    }

    return iResult;
  }

  String checkDevices() throws IOException {
    String strValue = "";
    int iIndex = 0;
    UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
    Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
    while (deviceIterator.hasNext()) {
      UsbDevice device = deviceIterator.next();
      iIndex++;
      strValue = strValue + String.valueOf(iIndex) + " DeviceClass:" + device.getDeviceClass() + "; DeviceId:"
          + device.getDeviceId() + "; DeviceName:" + device.getDeviceName() + "; VendorId:" + device.getVendorId() +
          "; \r\nProductId:" + device.getProductId() + "; InterfaceCount:" + device.getInterfaceCount()
          + "; describeContents:" + device.describeContents() + ";\r\n" +
          "DeviceProtocol:" + device.getDeviceProtocol() + ";DeviceSubclass:" + device.getDeviceSubclass() + ";\r\n";
      strValue = strValue + "****************\r\n";

    }
    if (strValue.equals("")) {
      strValue = "No USB device.";
    }

    return strValue;
  }

  BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if ((device.getProductId() == 8211 && device.getVendorId() == 1305)
            || (device.getProductId() == 8213 && device.getVendorId() == 1305)) {
          mUsbDriver.closeUsbDevice(device);
        }
      } else if (ACTION_USB_PERMISSION.equals(action))
        synchronized (this) {
          UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
          if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
            if ((device.getProductId() == 8211 && device.getVendorId() == 1305)
                || (device.getProductId() == 8213 && device.getVendorId() == 1305)) {
              // 赋权限以后的操作
            }
          } else {
            Toast.makeText(context, "permission denied for device" + device, Toast.LENGTH_SHORT).show();
          }
        }

    }
  };
}
