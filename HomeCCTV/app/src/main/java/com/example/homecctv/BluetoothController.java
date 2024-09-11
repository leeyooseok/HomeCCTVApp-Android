//package com.example.homecctv;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.content.Context;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.UUID;
//
//public class BluetoothController {
//
//    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private BluetoothAdapter bluetoothAdapter;
//    private BluetoothSocket bluetoothSocket;
//    private OutputStream outputStream;
//    private Context context;
//
//    public BluetoothController(Context context) {
//        this.context = context;
//        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//    }
//
//    public boolean connectToDevice(String deviceAddress) {
//
//        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
//
//        try {
//            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
//            bluetoothSocket.connect();
//            outputStream = bluetoothSocket.getOutputStream();
//            Toast.makeText(context, "Bluetooth Connected", Toast.LENGTH_SHORT).show();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//    }
//
//    public void sendCommand(String command) {
//        if (outputStream != null) {
//            try {
//                outputStream.write(command.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(context, "Command Failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    public void closeConnection() {
//        try {
//            if (outputStream != null) {
//                outputStream.close();
//            }
//            if (bluetoothSocket != null) {
//                bluetoothSocket.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
